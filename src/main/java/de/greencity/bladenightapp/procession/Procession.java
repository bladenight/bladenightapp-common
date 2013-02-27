package de.greencity.bladenightapp.procession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.procession.tasks.ComputeSchedulerClient;
import de.greencity.bladenightapp.procession.tasks.ParticipantCollectorClient;
import de.greencity.bladenightapp.routes.Route;
import de.greencity.bladenightapp.routes.Route.ProjectedLocation;

public class Procession implements ComputeSchedulerClient, ParticipantCollectorClient {
	public Procession() {
		init();
	}

	private void init() {
		participants = new ConcurrentHashMap<String, Participant>();
		headMovingPoint = new MovingPoint();
		tailMovingPoint = new MovingPoint();
		route = new Route();
		route.setName("<undefined default route>");

		// TODO move to the application configuration
		int nSegments = 200;
		headAndTailComputer = new HeadAndTailComputer(nSegments);
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	// Returned list is read-only!
	public List<Participant> getParticipants() {
		return new ArrayList<Participant>(participants.values());
	}

	@Override
	public synchronized void removeOutdatedParticipants(double factor) {
		if ( meanParticipantUpdatePeriod <= 0 )
			return;
		for ( String id : participants.keySet()) {
			Participant p = participants.get(id);
			long age = p.getLastLifeSignAge();
			if ( age > factor * meanParticipantUpdatePeriod  ) {
				participants.remove(id);
				getLog().info("Removing participant " + id + " " + age + " > " + factor * meanParticipantUpdatePeriod );
			}
		}
	}
	
	public void removeParticipant(String deviceId) {
		participants.remove(deviceId);
		headAndTailComputer.removeParticipant(deviceId);
	}

	public int getParticipantCount() {
		return participants.size();
	}

	public int getParticipantOnRoute() {
		int count = 0;
		for ( Participant p : participants.values()) {
			if ( p.isOnRoute() ) {
				count++;
			}
		}
		return count;
	}

	public synchronized Participant updateParticipant(ParticipantInput participantInput) {
		getLog().debug("updateParticipant: " + participantInput);
		String participantId = participantInput.getParticipantId();
		Participant participant = participants.get(participantId);
		if ( participant == null ) {
			if ( participants.size() == 0 ) {
				// Reset statistic:
				meanParticipantUpdatePeriod = 0;
			}
			participant = getOrCreateParticipant(participantId);
		}
		else {
			long age = participant.getLastLifeSignAge();
			if ( meanParticipantUpdatePeriod > 0 )
				meanParticipantUpdatePeriod = ( 9 * meanParticipantUpdatePeriod + age) / 10;
			else
				// No statistic available. Just use the actual as a reference
				meanParticipantUpdatePeriod = age;
		}

		List<ProjectedLocation> potentialLocations = route.projectPosition(participantInput.getLatitude(), participantInput.getLongitude());

		ParticipantUpdater updater = new ParticipantUpdater.Builder().
				setProcessionEnds(getTailPosition(), getHeadPosition()).
				setParticipantInput(participantInput).
				setParticipant(participant).
				setPotentialLocations(potentialLocations).
				setRouteLength(route.getLength()).
				build();

		updater.updateParticipant();
		
		if ( participant.isOnRoute() )
			headAndTailComputer.updateParticipant(participantId, participant.getLinearPosition(), participant.getLinearSpeed());
		else
			headAndTailComputer.removeParticipant(participantId);

		return participant;
	}

	// Result is read-only
	public Participant getParticipant(String id) {
		return participants.get(id);
	}

	private Participant getOrCreateParticipant(String id) {
		Participant p = participants.get(id);
		if ( p == null ) {
			p = new Participant();
			p.setDeviceId(id);
			participants.put(id, p);
		}
		return p;
	}

	public boolean isParticipantOnRoute(String deviceId) {
		Participant participant = getParticipant(deviceId);
		if ( participant == null)
			return false;
		return participant.isOnRoute();
	}


	public MovingPoint getHead() {
		return headMovingPoint;
	}

	public MovingPoint getTail() {
		return tailMovingPoint;
	}

	public double getLength() {
		return headMovingPoint.getLinearPosition() - tailMovingPoint.getLinearPosition();
	}

	@Override
	public synchronized void compute() {
		getLog().info("computeProcession");

		if ( route == null ) {
			getLog().error("computeProcession: no route available");
			return;
		}
		double routeLength = route.getLength();
		if ( routeLength == 0 ) {
			getLog().warn("computeProcession: route has zero length");
			return;
		}

		headAndTailComputer.setRouteLength(route.getLength());

		long startTime = System.currentTimeMillis();

		List<Participant> participantList = new ArrayList<Participant>(participants.values());
		getLog().info("computeProcession: " + participantList.size() + " participants are registered");

		if ( ! headAndTailComputer.compute() ) {
			getLog().info("computeProcession: could not find the procession position");
			headMovingPoint = new MovingPoint();
			tailMovingPoint = new MovingPoint();
			return;
		}

		MovingPoint newHeadMovingPoint = new MovingPoint();
		MovingPoint newTailMovingPoint = new MovingPoint();

		newHeadMovingPoint.update(0, 0, headAndTailComputer.getHeadPosition());
		newTailMovingPoint.update(0, 0, headAndTailComputer.getTailPosition());

		completeEndMovingPoint(newHeadMovingPoint, headMovingPoint);
		completeEndMovingPoint(newTailMovingPoint, tailMovingPoint);

		headMovingPoint = newHeadMovingPoint;
		tailMovingPoint = newTailMovingPoint;

		long endTime = System.currentTimeMillis();

		getLog().info("computeProcession: compute time: " + (endTime-startTime)+"ms");
	}

	protected void completeEndMovingPoint(MovingPoint newMp, MovingPoint lastMp) {
		newMp.isInProcession(true);
		newMp.isOnRoute(true);

		// Compute the speed of the head or the tail, but don't allow it to jump too much
		if ( lastMp != null && lastMp.isOnRoute() ) {
			double oldPos = lastMp.getLinearPosition();
			double newPos = newMp.getLinearPosition();
			newMp.setLinearPosition( updateSmoothingFactor * oldPos  + (1-updateSmoothingFactor) * newPos );

			double deltaT = (newMp.getTimestamp() - lastMp.getTimestamp()) / (3600.0 * 1000.0);
			double newSpeed = ( newPos - oldPos) / ( 1000.0 * deltaT);
			newMp.setLinearSpeed( updateSmoothingFactor * lastMp.getLinearSpeed() + (1-updateSmoothingFactor) * newSpeed );
		}
		getLog().info("newMp.linearPosition="+newMp.getLinearPosition());
		getLog().info("newMp.linearSpeed="+newMp.getLinearSpeed());
		Route.LatLong latLong = route.convertLinearPositionToLatLong(newMp.getLinearPosition());
		newMp.setLatLong(latLong.lat, latLong.lon);
	}

	public double getHeadPosition() {
		return headMovingPoint.getLinearPosition();
	}

	public double getTailPosition() {
		return tailMovingPoint.getLinearPosition();
	}

	public boolean isValid() {
		return getHead().isOnRoute() &&
				getTail().isOnRoute() &&
				getHead().getLinearPosition() >= getTail().getLinearPosition();  
	}

	public double getUpdateSmoothingFactor() {
		return updateSmoothingFactor;
	}

	public double evaluateTravelTimeBetween(double pos1, double pos2) {
		return 0;
	}
	
	/***
	 * Smoothen jumps of the head and the tail
	 * 0.0  : no smoothing
	 * 0.99 : unreasonable smoothing
	 */
	public void setUpdateSmoothingFactor(double updateSmoothingFactor) {
		this.updateSmoothingFactor = updateSmoothingFactor;
	}

	private Route route;
	private MovingPoint headMovingPoint;
	private MovingPoint tailMovingPoint;
	private HeadAndTailComputer headAndTailComputer;

	protected double updateSmoothingFactor = 0.0;


	private Map<String, Participant> participants;

	private static Log log;

	private double meanParticipantUpdatePeriod;

	public static void setLog(Log log) {
		Procession.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(Procession.class));
		return log;
	}

}
