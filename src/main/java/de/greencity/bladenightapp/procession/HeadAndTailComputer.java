package de.greencity.bladenightapp.procession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HeadAndTailComputer extends SegmentedLinearRoute implements ProcessionParticipantsListener {
	// TODO put the greeediness in the config file
	// final double processionGreediness = 3.8;
	final double processionGreediness = 3.8;

	private static class Segment {
		public double score;
	}

	static class ParticipantData {
		ParticipantData(double position, double speed) {
			this.position = position;
			this.speed = speed;
			// this.lastUpdate = System.currentTimeMillis();

		}
		public double position;
		public double speed;
		// public long lastUpdate;
	}

	HeadAndTailComputer(int nSegments) {
		super(nSegments);
		participantPositions = new ConcurrentHashMap<String, ParticipantData>();
	}

	@Override
	public void updateParticipant(String deviceId, double position, double speed) {
		participantPositions.put(deviceId, new ParticipantData(position,speed));
	}

	@Override
	public void removeParticipant(String deviceId) {
		getLog().debug("Removing participant " + deviceId);
		participantPositions.remove(deviceId);
	}

	private void prepareScoreMap() {
		segments = new Segment[getNumberOfSegments()];
		for(int segment=0; segment<getNumberOfSegments();segment++)
			segments[segment] = new Segment();
		for ( String deviceId : participantPositions.keySet() ) {
			ParticipantData data = participantPositions.get(deviceId);
			if ( data.position >= 0 && data.position <= getRouteLength() ) {
				getLog().debug("User " + deviceId + " is at " + data.position);
				int segment = getSegmentForLinearPosition(data.position);
				segments[segment].score++;
				// Moving participants get a bonus:
				if ( data.speed > 0 )
					segments[segment].score++;

				// TODO reenable bonus based on age
				// More recent updates get a bonus:
				// double referenceUpdateAge = 2 * meanUpdateAge;

				//			double age = participant.getLastLifeSignAge(); 
				//			if ( age <= referenceUpdateAge && referenceUpdateAge > 0) {
				//				double bonus = 0.5 * ( 1.0 - age / referenceUpdateAge );
				//				segments[segment].score += bonus;
				//			}
				getLog().debug("score["+segment+"]="+segments[segment].score);
			}
		}
	}

	/**
	 * Compute.
	 * Get the results with getHeadPosition() and getTailPosition()
	 * @return false in case of failure
	 */
	public boolean compute() {
		if ( getRouteLength() <= 0 )
			throw new IllegalStateException("Invalid route length: " + getRouteLength());

		prepareScoreMap();
		
		if ( ! computeHeadAndTail() )
			return false;

		return true;
	}

	private boolean computeHeadAndTail() {
		int bestTailSegment = -1, bestHeadSegment = -1;
		double bestScore = 0;
		double globalScore = getGlobalScore();

		getLog().debug("getNumberOfSegments()="+getNumberOfSegments());
		for ( int tailSegment=0; tailSegment<getNumberOfSegments(); tailSegment++) {
			getLog().debug("segment " + tailSegment + "  score: " + segments[tailSegment].score);
			for ( int headSegment=tailSegment; headSegment<getNumberOfSegments(); headSegment++) {
				double localSum = 0;
				for ( int i=tailSegment; i<=headSegment ; i++ ) {
					localSum += segments[i].score;
				}
				double relativeSum = localSum/globalScore;
				double score = Math.pow(relativeSum, processionGreediness) / ((headSegment-tailSegment+1)*1.0/getNumberOfSegments());
				if ( score > bestScore ) {
					bestScore = score;
					bestTailSegment = tailSegment;
					bestHeadSegment = headSegment;
				}
			}
		}

		if ( bestHeadSegment < 0 || bestTailSegment < 0 ) {
			getLog().debug("could not find the procession position");
			return false;
		}
		
		if ( bestHeadSegment < getNumberOfSegments() - 1)
			bestHeadSegment++; // let's put the head at the head of the segment we found

		getLog().debug("Best: " + bestTailSegment+"-"+bestHeadSegment+" : " + bestScore);

		double tailSegmentPosition = bestTailSegment * getRouteLength() / getNumberOfSegments();
		double headSegmentPosition = bestHeadSegment * getRouteLength() / getNumberOfSegments();

		tailPosition = headSegmentPosition;
		headPosition = tailSegmentPosition;

		for (ParticipantData participantData : participantPositions.values()) {
			double participantPosition = participantData.position;
			if ( participantPosition >= tailSegmentPosition && participantPosition <= headSegmentPosition ) {
				headPosition = Math.max(headPosition, participantPosition);
				tailPosition = Math.min(tailPosition, participantPosition);
			}
		}

		getLog().debug("final positions: " + tailPosition + "-"  + headPosition);

		return true;
	}

	public double getHeadPosition() {
		return headPosition;
	}

	public double getTailPosition() {
		return tailPosition;
	}

	private double getGlobalScore() {
		double globalScore = 0;
		for(int segment=0; segment<getNumberOfSegments(); segment++)
			globalScore += segments[segment].score;
		return globalScore;
	}


	private Segment[] segments;
	private Map<String, ParticipantData> participantPositions;
	private double headPosition;
	private double tailPosition;
	// private long meanUpdateAge;

	private static Log log;

	public static void setLog(Log log) {
		HeadAndTailComputer.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(HeadAndTailComputer.class));
		return log;
	}

}
