package de.greencity.bladenightapp.procession;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.time.Clock;
import de.greencity.bladenightapp.time.SystemClock;

public class SpeedMapComputer extends SegmentedLinearRoute implements ProcessionParticipantsListener {

	private static class Segment {
		public double meanTravelTime;
	}

	private static class ParticipantData {
		ParticipantData(long lastUpdate, double position, double speed) {
			this.position = position;
			this.speed = speed;
			this.lastUpdate = lastUpdate;

		}
		public double position;
		public double speed;
		public long lastUpdate;
	}

	private Segment[] segments;

	SpeedMapComputer(int nSegment) {
		super(nSegment);
		segments = new Segment[nSegment];
		for(int segment=0; segment<nSegment;segment++)
			segments[segment] = new Segment();
		participantPositions = new ConcurrentHashMap<String, ParticipantData>();
	}

	@Override
	public synchronized void updateParticipant(String deviceId, double position, double speed) {
		ParticipantData data = participantPositions.get(deviceId);
		if ( data == null ) {
			participantPositions.put(deviceId, new ParticipantData(clock.currentTimeMillis(), position,speed));
			return;
		}
		long updateTime = clock.currentTimeMillis();
		updateMapBasedOnParticipantUpdate(updateTime, data, deviceId, position);
		data.speed = speed;
		data.position = position;
		data.lastUpdate = updateTime;
	}

	private void updateMapBasedOnParticipantUpdate(long updateTime, ParticipantData data, String deviceId, double newPosition) {
		long currentTime = clock.currentTimeMillis();
		long deltaTime = (currentTime -  data.lastUpdate);
		
		if ( deltaTime == 0 ) {
			return;
		}
		if ( deltaTime < 0 ) {
			getLog().warn("Clock skew detected: " + currentTime + "/" + data.lastUpdate);
			return;
		}
		
		double oldPosition = data.position;
		double meanSegmentTravelTime =  deltaTime * (getSegmentLength() / Math.abs(newPosition - oldPosition));
		int startSegment = getSegmentForLinearPosition(Math.min(oldPosition, newPosition));
		int endSegment = getSegmentForLinearPosition(Math.max(oldPosition, newPosition));
		for ( int segment = startSegment ; segment <= endSegment ; segment++ ) {
			if ( segments[segment].meanTravelTime == 0 )
				segments[segment].meanTravelTime = meanSegmentTravelTime;
			else
				segments[segment].meanTravelTime = ( meanSegmentTravelTime + segments[segment].meanTravelTime ) / 2.0;
			getLog().debug("segment="+segment + " " + segments[segment].meanTravelTime);
		}
			
	}
	
	@Override
	public void removeParticipant(String deviceId) {
		getLog().debug("Removing participant " + deviceId);
		participantPositions.remove(deviceId);
	}


	public Clock getClock() {
		return clock;
	}

	public void setClock(Clock clock) {
		this.clock = clock;
	}

	public double evaluateTravelTimeBetween(double position1, double position2) {
		if ( position2 <= position1 )
			return 0.0;
		
		double time = 0;
		int startSegment = getSegmentForLinearPosition(position1);
		int endSegment = getSegmentForLinearPosition(position2);
		for ( int segment = startSegment ; segment <= endSegment ; segment ++) {
			double segmentMtt = segments[segment].meanTravelTime;
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
			getLog().debug("segment="+segment + " mtt=" + segmentMtt + " sum=" + time);
		}
		return time;
	}

	private Clock clock = new SystemClock();
	private ConcurrentHashMap<String, ParticipantData> participantPositions;
	
	private static Log log;

	public static void setLog(Log log) {
		SpeedMapComputer.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(SpeedMapComputer.class));
		return log;
	}
}
