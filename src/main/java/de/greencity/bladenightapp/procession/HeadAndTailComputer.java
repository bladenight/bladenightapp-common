package de.greencity.bladenightapp.procession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HeadAndTailComputer extends SegmentedLinearRoute implements ProcessionParticipantsListener {
	private static class Segment {
		public double score;
	}

	HeadAndTailComputer(int nSegments) {
		super(nSegments);
		participantPositions = new ConcurrentHashMap<String, ParticipantData>();
	}

	@Override
	public void updateParticipant(String deviceId, ParticipantData participantData) {
		participantPositions.put(deviceId, participantData);
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
				getLog().debug("User " + deviceId + " acc=" + data.accuracy);
				int segment = getSegmentForLinearPosition(data.position);
				
				double participantScore = 1.0; 
				
				// Prefer moving participants:
				if ( data.speed > 0.0 )
					participantScore *= movingBonus;

				participantScore *= accuracyBonus(data.accuracy);

				segments[segment].score += participantScore;
			}
		}
	}
	
	// TODO replace these hard-coded pragmatic values by something better 
	private double accuracyBonus(double accuracy) {
		if ( accuracy <= 0.0 ) // no value available
			return 0.5;
		if ( accuracy <= 30.0 )
			return 1.0;
		if ( accuracy <= 100.0 )
			return 0.5;
		if ( accuracy <= 500.0 )
			return 0.1;
		return 0.05;
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

		// getLog().debug("getNumberOfSegments()="+getNumberOfSegments());
		for ( int tailSegment=0; tailSegment<getNumberOfSegments(); tailSegment++) {
			// getLog().debug("segment " + tailSegment + "  score: " + segments[tailSegment].score);
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
			getLog().trace("could not find the procession position");
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

	public double getProcessionGreediness() {
		return processionGreediness;
	}

	/***
	 * The lower the greediness, the more likely some participants will be ignored
	 * 
	 */
	public void setProcessionGreediness(double processionGreediness) {
		this.processionGreediness = processionGreediness;
	}


	private Segment[] segments;
	private Map<String, ParticipantData> participantPositions;
	private double headPosition;
	private double tailPosition;
	double processionGreediness = 6;
	double movingBonus = 3;

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
