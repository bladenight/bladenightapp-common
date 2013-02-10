package de.greencity.bladenightapp.procession;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.routes.Route;
import de.greencity.bladenightapp.routes.Route.ProjectedLocation;

/**
 * Updates a participant based on his previous location, the route, and other dynamic values.
 * Typically called when the device of the participant calls in to update its position. 
 */
public class ParticipantUpdater {

	public ParticipantUpdater(Procession procession, Participant participant, ParticipantInput input) {
		this.procession = procession;
		this.participant = participant;
		this.route = procession.getRoute();
		this.input = input;
	}

	public boolean updateParticipant() {

		long timestamp = getNewTimestamp();

		participant.setLastLifeSign(timestamp);

		String deviceId = input.getParticipantId();

		//		if ( input.getTimestamp() == 0 ) {
		//			getLog().debug("User doesn't have a proper fix.");
		//			return true;
		//		}

		ProjectedLocation locationOnRoute = findBestNewLocationOnRoute();

		MovingPoint point = participant.getLastKnownPoint();

		if ( locationOnRoute != null ) {
			getLog().debug("update: findBestNewLocationOnRoute("+deviceId+") returned " + locationOnRoute.linearPosition);
			point.update(input.getLatitude(), input.getLongitude(), locationOnRoute.linearPosition);
			point.isOnRoute(true);
			point.isInProcession(true);
		}
		else {
			getLog().debug("Position on the route couldn't be determined");
			point.setLatitude(input.getLatitude());
			point.setLongitude(input.getLongitude());
			point.isOnRoute(false);
			point.isInProcession(false);
			point.setTimestamp(point.getTimestamp());
		}

		return true;
	}

	public long getNewTimestamp(){
		return System.currentTimeMillis();
	}


	protected ProjectedLocation findBestNewLocationOnRoute() {

		List<ProjectedLocation> currentCandidateList = route.projectPosition(input.getLatitude(), input.getLongitude());

		if ( currentCandidateList.size() == 0 ) {
			getLog().debug("findBestNewLocationOnRoute: User is out of corridor !");
			return null;
		}

		for ( ProjectedLocation l : currentCandidateList ) {
			l.evaluation = 1.0;
			l.evaluation *= evaluateCandidateOnDistanceToSegment(l);
			l.evaluation *= evaluateCandidateOnDistanceToStart(l);
			l.evaluation *= evaluateCandidateOnDistanceToPrevious(l);
			l.evaluation *= evaluateCandidateOnDistanceToProcession(l);
		}


		// Sort by evaluation:
		Collections.sort(currentCandidateList, new Comparator<ProjectedLocation>() {
			public int compare(ProjectedLocation o1, ProjectedLocation o2) {
				return (int)Math.signum(o2.evaluation - o1.evaluation);
			}
		}
				);


		getLog().debug("Evaluated potential new locations for " + participant.getDeviceId());
		for ( ProjectedLocation l : currentCandidateList ) {
			getLog().debug("pos="+l.linearPosition);
			getLog().debug("  segm="+l.segment);
			getLog().debug("  dist="+l.distanceToSegment);
			getLog().debug("  eval="+l.evaluation);
		}

		return currentCandidateList.get(0);
	}

	protected double evaluateCandidateOnDistanceToSegment(ProjectedLocation candidate) {
		final double referenceDistance = 100.0;
		final double referenceEvaluation = 0.8;
		final double alpha = ( 1 - referenceEvaluation ) / ( referenceDistance * referenceEvaluation);

		double evaluation = capEvaluation(1.0 / ( 1 + alpha * candidate.distanceToSegment));
		getLog().debug("evaluateCandidateOnDistanceToSegment: "+evaluation);
		return evaluation;
	}

	protected double evaluateCandidateOnDistanceToStart(ProjectedLocation candidate) {
		double routeLength = route.getLength();
		double evaluation = capEvaluation(1.0 -  0.5 * candidate.linearPosition / routeLength);
		getLog().debug("evaluateCandidateOnDistanceToStart: "+evaluation);
		return capEvaluation(evaluation);
	}

	protected double evaluateCandidateOnDistanceToPrevious(ProjectedLocation candidate) {

		if ( ! participant.getLastKnownPoint().isOnRoute() )
			return 1.0;

		getLog().debug("evaluateCandidateOnDistanceToPrevious: several segments available, and previous position is known " + participant.getLinearPosition());

		double evaluation = 1.0;
		if ( candidate.linearPosition < participant.getLinearPosition() ) {
			evaluation =  1.0 - ( participant.getLinearPosition() - candidate.linearPosition  ) / route.getLength();
			evaluation = Math.pow(evaluation,3.0);
		}

		getLog().debug("evaluateCandidateOnDistanceToPrevious: "+evaluation);
		return evaluation;
	}

	protected double evaluateCandidateOnDistanceToProcession(ProjectedLocation candidate) {
		if ( ! procession.isValid() )
			return 1.0;

		double head = procession.getHeadPosition();
		double tail = procession.getTailPosition();

		getLog().debug("evaluateCandidateOnDistanceToProcession: procession position is known ( "+tail+" / " + head + " )");

		double processionLength = procession.getLength();
		double processionMiddle = (head + tail)/2.0;
		double distanceToMiddle = Math.abs(processionMiddle - candidate.linearPosition);
		final double referenceDistance = processionLength * 1.5;
		final double referenceEvaluation = 0.1;
		final double alpha = ( 1 - referenceEvaluation ) / ( referenceDistance * referenceEvaluation);
		getLog().debug("evaluateCandidateOnDistanceToProcession: alpha="+alpha);
		double evaluation = capEvaluation(1.0 / ( 1 + alpha * distanceToMiddle)); 
		getLog().debug("evaluateCandidateOnDistanceToProcession: "+evaluation);
		return evaluation;
	}

	protected double capEvaluation(double evaluation) {
		if ( evaluation < 0.0 )
			return 0.0;
		else if ( evaluation > 1.0 )
			return 1.0;
		else
			return evaluation;
	}


	private static Log log;

	public static void setLog(Log log) {
		ParticipantUpdater.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(ParticipantUpdater.class));
		return log;
	}

	private Route route;
	private Procession procession;
	private Participant participant;
	private ParticipantInput input;


}
