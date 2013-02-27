package de.greencity.bladenightapp.procession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

	private static class Segment {
		public double score;
		public double meanTravelTime; 
	}

	private static class ParticipantData {
		ParticipantData(double position) {
			this.position = position;
			this.lastUpdate = System.currentTimeMillis();
		}
		public double position;
		public long lastUpdate;
	}

	// TODO put the greeediness in the config file
	// final double processionGreediness = 3.8;
	final double processionGreediness = 3.8;

	SegmentedProcession(int nSegment) {
		this.nSegment = nSegment;
		participantPositions = new ConcurrentHashMap<String, ParticipantData>();
		reinitializeDynamicData();
	}

	private void reinitializeDynamicData() {
		segments = new Segment[nSegment];
		for(int segment=0; segment<nSegment;segment++)
			segments[segment] = new Segment();
		previousParticipantInfos = participantPositions;
		participantPositions = new ConcurrentHashMap<String, ParticipantData>();
	}

	public void setRouteLength(double routeLength) {
		this.routeLength = routeLength;
	}

	public void updateParticipants(List<Participant> participants) {
		if ( routeLength <= 0 )
			throw new IllegalStateException("Invalid route length. Please call setRouteLength() first");

		reinitializeDynamicData();
		long sum=0;
		long count=0;
		long referenceTime = System.currentTimeMillis();
		for ( Participant participant : participants) {
			updateParticipant(participant);
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

	private void updateParticipant(Participant participant) {
		MovingPoint lastPos = participant.getLastKnownPoint();
		getLog().debug("SegmentedProcession: " + lastPos);
		if ( lastPos != null && lastPos.isOnRoute() ) {
			getLog().debug("SegmentedProcession: a user is at " + lastPos.getLinearPosition());
			updateMeanTravelTime(participant);
			int segment = getSegmentForLinearPosition(lastPos.getLinearPosition());
			segments[segment].score++;
			participantPositions.put(participant.getDeviceId(), new ParticipantData(lastPos.getLinearPosition()));
			// Moving participants get a bonus:
			if ( lastPos.getLinearSpeed() > 0 )
				segments[segment].score++;
			// More recent updates get a bonus:
			double referenceUpdateAge = 2 * meanUpdateAge;
			double age = participant.getLastLifeSignAge(); 
			if ( age <= referenceUpdateAge && referenceUpdateAge > 0) {
				double bonus = 0.5 * ( 1.0 - age / referenceUpdateAge );
				segments[segment].score += bonus;
			}
			getLog().debug("SegmentedProcession: score="+segments[segment].score);
		}
	}

	private void updateMeanTravelTime(Participant participant) {
		ParticipantData previousParticipantData = previousParticipantInfos.get(participant.getDeviceId());
		if ( previousParticipantData == null )
			return;
		long deltaTime = System.currentTimeMillis() - previousParticipantData.lastUpdate;
//		System.out.println("** lastUpdate="+previousParticipantData.lastUpdate);		
//		System.out.println("deltaTime="+deltaTime);		
		updateMeanTravelTime(previousParticipantData.position, participant.getLinearPosition(), deltaTime);
	}

	private void updateMeanTravelTime(double previousPosition, double newPosition, long deltaTime) {
//		System.out.println("updateMeanTravelTime("+previousPosition+","+newPosition+","+deltaTime+")");
		if ( newPosition <= previousPosition )
			return;
		if ( deltaTime <= 0 )
			return;

		int startSegment = getSegmentForLinearPosition(previousPosition);
		int endSegment = getSegmentForLinearPosition(newPosition);

		double segmentLength = getSegmentLength();
		double speed = (newPosition - previousPosition ) / deltaTime; 
//		System.out.println("deltaTime="+deltaTime);
//		System.out.println("speed="+speed);
		double travelTimePerSegment = segmentLength / speed;  
		for ( int segment = startSegment ; segment <= endSegment ; segment++) {
			if ( segments[segment].meanTravelTime == 0 ) {
				segments[segment].meanTravelTime = travelTimePerSegment;
			}
			else {

				double weight = 1.0;
				if ( segment == startSegment && segment == endSegment ) {
					weight = ( newPosition - previousPosition ) / segmentLength; 
				}
				else if ( segment == startSegment ) {
					weight = (getPositionOfSegmentEnd(startSegment) - previousPosition ) / segmentLength; 
				}
				else if ( segment == endSegment ) {
					weight = (newPosition - getPositionOfSegmentEnd(endSegment) ) / segmentLength; 
				}
//				System.out.println("weight="+weight);
				segments[segment].meanTravelTime = ( weight * travelTimePerSegment + segments[segment].meanTravelTime ) / ( 1 + weight );
			}			
//			System.out.println("segments["+segment+"].meanTravelTime="+segments[segment].meanTravelTime);
		}
	}

	public double evaluateTravelTimeBetween(double pos1, double pos2) {
//		System.out.println("evaluateTravelTimeBetween("+pos1+","+pos2+")");
//		System.out.println("routeLength="+routeLength);
		if ( routeLength <= 0 )
			throw new IllegalStateException("Invalid route length. Please call setRouteLength() first");

		if ( pos2 < pos1 )
			return 0.0;

		int startSegment = getSegmentForLinearPosition(pos1);
		int endSegment = getSegmentForLinearPosition(pos2);

//		System.out.println("startSegment="+startSegment);
//		System.out.println("endSegment="+endSegment);

		double segmentLength = getSegmentLength();

		double result = 0.0;
		for ( int segment = startSegment ; segment <= endSegment ; segment++) {
//			System.out.println("segment="+segment);
			double weight = 1.0;
			if ( segment == startSegment && segment == endSegment ) {
				weight = ( pos2 - pos1 ) / segmentLength; 
			}
			else if ( segment == startSegment ) {
				weight = (getPositionOfSegmentEnd(startSegment) - pos1) / segmentLength; 
			}
			else if ( segment == endSegment ) {
				weight = (pos2 - getPositionOfSegmentStart(endSegment) ) / segmentLength; 
			}
//			System.out.println("weight="+weight);
			result += weight * segments[segment].meanTravelTime; 
//			System.out.println("result="+result);
		}
		return result;
	}

	public int getSegmentForLinearPosition(double linearPosition) {
		int segment = (int)( linearPosition * nSegment / routeLength);
		if ( segment >= nSegment )
			segment = nSegment - 1;
		if ( segment <= 0 )
			segment = 0;
		return segment;
	}

	public double getPositionOfSegmentStart(int segment) {
		return segment * getSegmentLength(); 
	}

	public double getPositionOfSegmentEnd(int segment) {
		return getPositionOfSegmentStart(segment+1); 
	}

	public double getSegmentLength() {
		return routeLength / nSegment; 
	}

	private double getGlobalScore() {
		double globalScore = 0;
		for(int segment=0; segment<nSegment; segment++)
			globalScore += segments[segment].score;
		return globalScore;
	}

	/**
	 * Compute.
	 * Get the results with getHeadPosition() and getTailPosition()
	 * @return false in case of failure
	 */
	public boolean computeHeadAndTail() {
		int bestTailSegment = -1, bestHeadSegment = -1;
		double bestScore = 0;
		double globalScore = getGlobalScore();

		getLog().debug("SegmentedProcession: nSegment="+nSegment);
		for ( int tailSegment=0; tailSegment<nSegment; tailSegment++) {
			getLog().debug("SegmentedProcession: segment " + tailSegment + "  score: " + segments[tailSegment].score);
			for ( int headSegment=tailSegment; headSegment<nSegment; headSegment++) {
				double localSum = 0;
				for ( int i=tailSegment; i<=headSegment ; i++ ) {
					localSum += segments[i].score;
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
			getLog().debug("SegmentedProcession: could not find the procession position");
			return false;
		}
		if ( bestHeadSegment < nSegment - 1)
			bestHeadSegment++; // let's put the head at the head of the segment we found

		getLog().debug("SegmentedProcession: Best: " + bestTailSegment+"-"+bestHeadSegment+" : " + bestScore);

		double tailSegmentPosition = bestTailSegment * routeLength / nSegment;
		double headSegmentPosition = bestHeadSegment * routeLength / nSegment;

		tailPosition = headSegmentPosition;
		headPosition = tailSegmentPosition;

		for (ParticipantData participantData : participantPositions.values()) {
			double participantPosition = participantData.position;
			if ( participantPosition >= tailSegmentPosition && participantPosition <= headSegmentPosition ) {
				headPosition = Math.max(headPosition, participantPosition);
				tailPosition = Math.min(tailPosition, participantPosition);
			}
		}

		getLog().debug("SegmentedProcession: final positions: " + tailPosition + "-"  + headPosition);

		return true;
	}

	public double getHeadPosition() {
		return headPosition;
	}

	public double getTailPosition() {
		return tailPosition;
	}

	private int nSegment;
	private Segment[] segments;
	private double routeLength;
	private Map<String, ParticipantData> participantPositions;
	private Map<String, ParticipantData> previousParticipantInfos;
	private double headPosition;
	private double tailPosition;
	private long meanUpdateAge;

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