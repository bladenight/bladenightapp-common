package de.greencity.bladenightapp.procession;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Computes the head and tail of a procession given a route and the positions of the participants.
 * It uses a simple but effective algorithm:
 * - the route is split in segments of equal length 
 * - each segment gets a score based on the participant data
 * - a optimization algorithm tries to maximize a score by selecting head and tail segments 
 */
class SegmentedProcession {
	
	// TODO put the greeediness in the config file
	// final double processionGreediness = 3.8;
	final double processionGreediness = 3.8;
	
	SegmentedProcession(double routeLength, int nSegment) {
		scores = new long[nSegment];
		this.routeLength = routeLength;
		this.nSegment = nSegment;
		participantPositions = new ArrayList<Double>();
	}

	public void addParticipants(List<Participant> participants) {
		long sum=0;
		long count=0;
		long referenceTime = System.currentTimeMillis();
		for ( Participant participant : participants) {
			addParticipant(participant);
			count++;
			long diff = referenceTime - participant.getLastLifeSign();
			if ( diff > 0 )
				sum += diff; 
		}
		if (count > 0 )
			meanUpdateAge = (long) (sum / count);
		else
			meanUpdateAge = 0;
	}

	private void addParticipant(Participant participant) {
		MovingPoint lastPos = participant.getLastKnownPoint();
		getLog().debug("computeProcession: " + lastPos);
		if ( lastPos != null && lastPos.isOnRoute() ) {
			getLog().debug("computeProcession: a user is at " + lastPos.getLinearPosition());
			int segment = getSegmentForLinearPosition(lastPos.getLinearPosition());
			getLog().debug("computeProcession: segment="+segment);
			scores[segment]++;
			participantPositions.add(lastPos.getLinearPosition());
			// Moving participants get a bonus:
			if ( lastPos.getLinearSpeed() > 0 )
				scores[segment]++;
			// More recent updates get a bonus:
			double referenceUpdateAge = 2 * meanUpdateAge;
			if ( participant.getLastLifeSignAge() <= referenceUpdateAge )
				scores[segment] += 0.5 * ( 1.0 - participant.getLastLifeSignAge() / referenceUpdateAge );
			globalScore += scores[segment]; 
		}
	}
	
	public int getSegmentForLinearPosition(double linearPosition) {
		int segment = (int)( linearPosition * nSegment / routeLength);
		if ( segment >= nSegment )
			segment = nSegment - 1;
		if ( segment <= 0 )
			segment = 0;
		return segment;
	}
	
	/**
	 * Compute.
	 * Get the results with getHeadPosition() and getTailPosition()
	 * @return false in case of failure
	 */
	public boolean computeHeadAndTail() {
		int bestTailSegment = -1, bestHeadSegment = -1;
		double bestScore = 0;

		for ( int tailSegment=0; tailSegment<nSegment; tailSegment++) {
			getLog().debug("computeProcession: segment " + tailSegment + "  score: " + scores[tailSegment]);
			for ( int headSegment=tailSegment; headSegment<nSegment; headSegment++) {
				double localSum = 0;
				for ( int i=tailSegment; i<=headSegment ; i++ ) {
					localSum += scores[i];
				}
				double relativeSum = localSum/globalScore;
				double score = Math.pow(relativeSum, processionGreediness) / ((headSegment-tailSegment+1)*1.0/nSegment);
				if ( score > bestScore ) {
					bestScore = score;
					bestTailSegment = tailSegment;
					bestHeadSegment = headSegment;
				}
			}
		}
		
		if ( bestHeadSegment < 0 || bestTailSegment < 0 ) {
			getLog().debug("computeProcession: could not find the procession position");
			return false;
		}
		if ( bestHeadSegment < nSegment - 1)
			bestHeadSegment++; // let's put the head at the head of the segment we found

		getLog().debug("computeProcession: Best: " + bestTailSegment+"-"+bestHeadSegment+" : " + bestScore);

		double tailSegmentPosition = bestTailSegment * routeLength / nSegment;
		double headSegmentPosition = bestHeadSegment * routeLength / nSegment;

		tailPosition = headSegmentPosition;
		headPosition = tailSegmentPosition;
		
		for (double participantPosition : participantPositions) {
			if ( participantPosition >= tailSegmentPosition && participantPosition <= headSegmentPosition ) {
				headPosition = Math.max(headPosition, participantPosition);
				tailPosition = Math.min(tailPosition, participantPosition);
			}
		}
		
		getLog().debug("computeProcession: final positions: " + tailPosition + "-"  + headPosition);
		
		return true;
	}

	public double getHeadPosition() {
		return headPosition;
	}

	public double getTailPosition() {
		return tailPosition;
	}

	protected int nSegment;
	protected long[] scores;
	protected double routeLength;
	protected List<Double> participantPositions;
	protected long globalScore;
	protected double headPosition;
	protected double tailPosition;
	protected long meanUpdateAge;

	private static Log log;

	public static void setLog(Log log) {
		SegmentedProcession.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(SegmentedProcession.class));
		return log;
	}
}