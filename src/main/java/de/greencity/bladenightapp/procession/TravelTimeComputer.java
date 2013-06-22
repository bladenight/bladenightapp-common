package de.greencity.bladenightapp.procession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.math.MedianFinder;
import de.greencity.bladenightapp.math.MedianFinder.WeightedValue;
import de.greencity.bladenightapp.time.Clock;
import de.greencity.bladenightapp.time.SystemClock;

public class TravelTimeComputer extends SegmentedLinearRoute implements ProcessionParticipantsListener {

	private static class Segment {
		public long lastUpdate;
		public double meanTravelTime;
	}

	private static class ParticipantStatistics {
		ParticipantStatistics(int nSegments, long lastUpdate, double position) {
			this.position = position;
			this.lastUpdate = lastUpdate;
			this.segments = TravelTimeComputer.newSegmentArray(nSegments);
		}
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
		public double position;
		public long lastUpdate;
		public Segment[] segments;
	}

	TravelTimeComputer(int nSegments) {
		super(nSegments);
		this.segments = TravelTimeComputer.newSegmentArray(nSegments);
		participantPositions = new ConcurrentHashMap<String, ParticipantStatistics>();
	}

	static Segment[] newSegmentArray(int size) {
		Segment[] segments = new Segment[size];
		for(int segment=0; segment<size;segment++)
			segments[segment] = new Segment();
		return segments;
	}
	
	@Override
	public synchronized void updateParticipant(String deviceId, ParticipantData participantData) {
		ParticipantStatistics statistics = participantPositions.get(deviceId);
		if ( statistics == null ) {
			participantPositions.put(deviceId, new ParticipantStatistics(getNumberOfSegments(), clock.currentTimeMillis(), participantData.position));
			return;
		}
		long updateTime = clock.currentTimeMillis();
		getLog().trace("deviceId="+deviceId);
		updateMapBasedOnParticipantUpdate(updateTime, statistics, deviceId, participantData.position);
		statistics.position = participantData.position;
		statistics.lastUpdate = updateTime;
	}

	private void updateMapBasedOnParticipantUpdate(long updateTime, ParticipantStatistics data, String deviceId, double newPosition) {
		getLog().trace("** updateMapBasedOnParticipantUpdate for "+deviceId);
		long currentTime = clock.currentTimeMillis();
		long deltaTime = (currentTime -  data.lastUpdate);

		if ( deltaTime == 0 ) {
			return;
		}
		if ( deltaTime < 0 ) {
			getLog().error("Clock skew detected: " + currentTime + "/" + data.lastUpdate);
			return;
		}

		double oldPosition = data.position;

		if ( newPosition == oldPosition )
			return;

		if (newPosition < oldPosition) {
			getLog().debug("Participant going back " + deviceId + "  " + oldPosition + " -> " + newPosition);
			return;
		}

		double meanSegmentTravelTime =  deltaTime * (getSegmentLength() / (newPosition - oldPosition) );

		getLog().debug("oldPosition           = "+oldPosition);
		getLog().debug("newPosition           = "+newPosition);
		getLog().debug("segmentLength         = "+getSegmentLength());
		getLog().debug("deltaTime             = "+deltaTime);
		getLog().debug("meanSegmentTravelTime = "+meanSegmentTravelTime);
		int startSegment = getSegmentForLinearPosition(oldPosition);
		int endSegment = getSegmentForLinearPosition(newPosition);
		for ( int segment = startSegment ; segment <= endSegment ; segment++ ) {
			if ( data.segments[segment].meanTravelTime == 0 )
				data.segments[segment].meanTravelTime = meanSegmentTravelTime;
			else
				data.segments[segment].meanTravelTime = ( meanSegmentTravelTime + data.segments[segment].meanTravelTime ) / 2.0;
			data.segments[segment].lastUpdate = updateTime;
			getLog().debug("final mtt["+segment + "] for part = " + data.segments[segment].meanTravelTime);
		}

	}

	@Override
	public void removeParticipant(String deviceId) {
		// Nothing to do
	}


	public Clock getClock() {
		return clock;
	}

	public void setClock(Clock clock) {
		this.clock = clock;
	}

	public synchronized void computeTravelTimeForAllSegments() {
		computeTravelTimeForAllSegments(0.5);
	}

	public synchronized void computeTravelTimeForAllSegments(double quantil) {
		long clockTime = clock.currentTimeMillis();				
		long startTime = System.currentTimeMillis();				

		Segment[] newSegments = TravelTimeComputer.newSegmentArray(getNumberOfSegments());

		for ( int segment = 0 ; segment < getNumberOfSegments() ; segment ++) {
			getLog().trace("** computeTravelTimeForAllSegments segment="+segment);
			MedianFinder medianFinder = new MedianFinder();
			List<WeightedValue> weightedValues = new ArrayList<WeightedValue>();

			newSegments[segment].lastUpdate = clockTime;

			long minLastUpdate = 0;
			for (ParticipantStatistics participantData : participantPositions.values()) {
				getLog().trace("  computeTravelTimeForAllSegments: meanTravelTime=" + participantData.segments[segment].meanTravelTime);
				if ( participantData.segments[segment].meanTravelTime > 0.0 )
					weightedValues.add(new WeightedValue(participantData.segments[segment].meanTravelTime, participantData.segments[segment].lastUpdate));
				minLastUpdate = Math.min(participantData.segments[segment].lastUpdate, minLastUpdate);
			}
			
			for (WeightedValue weightedValue : weightedValues ) {
				weightedValue.weight = (weightedValue.weight - minLastUpdate ) * 1.0 / (clockTime - minLastUpdate + 1);
				medianFinder.addWeightedValue(weightedValue);
			}
			getLog().trace("  computeTravelTimeForAllSegments: got " + medianFinder.sampleCount() + " participant samples for segment " + segment);
			if ( medianFinder.sampleCount() > 0 )
				newSegments[segment].meanTravelTime = medianFinder.findMedian(quantil);
			else
				newSegments[segment].meanTravelTime = 0.0;
		}
		segments = newSegments;
		updateMeanTravelTimeOverAllSegments();
		getLog().info("** computeTravelTimeForAllSegments finished in "+ (System.currentTimeMillis() - startTime) + "ms");
	}

	public double evaluateTravelTimeBetween(double position1, double position2) {
		if ( position2 <= position1 )
			return 0.0;

		double time = 0;
		int startSegment = getSegmentForLinearPosition(position1);
		int endSegment = getSegmentForLinearPosition(position2);
		for ( int segment = startSegment ; segment <= endSegment ; segment ++) {
			double segmentMtt = segments[segment].meanTravelTime;
			if ( segmentMtt <= 0 )
				segmentMtt = meanTravelTimeOverAllSegments;
			double weight;
			if ( segment == startSegment && segment == endSegment ) {
				weight = ( position2 - position1 ) / getSegmentLength(); 
			}
			else if ( segment == startSegment ) {
				weight = (getPositionOfSegmentEnd(segment) - position1) / getSegmentLength(); 
			}
			else if ( segment == endSegment ) {
				weight = (position2 - getPositionOfSegmentStart(segment) ) / getSegmentLength(); 
			}
			else {
				weight = 1.0;
			}
			time += weight * segmentMtt;
			getLog().trace("segment="+segment + " mtt=" + segmentMtt + " sum=" + time);
		}
		return time;
	}

	private void updateMeanTravelTimeOverAllSegments() {
		MedianFinder medianFinder = new MedianFinder();
		for ( int segment = 0 ; segment < getNumberOfSegments() ; segment ++) {
			double segmentMtt = segments[segment].meanTravelTime;
			if ( segmentMtt > 0.0 )
				medianFinder.addValue(segmentMtt);
		}
		if ( medianFinder.sampleCount() > 0)
			meanTravelTimeOverAllSegments = medianFinder.findMedian();
		getLog().trace("meanTravelTimeOverAllSegments = "+meanTravelTimeOverAllSegments);
	}
	

	private Clock clock = new SystemClock();
	private ConcurrentHashMap<String, ParticipantStatistics> participantPositions;
	private static Log log;
	private Segment[] segments;
	private double meanTravelTimeOverAllSegments = 0;



	public static void setLog(Log log) {
		TravelTimeComputer.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(TravelTimeComputer.class));
		return log;
	}
}
