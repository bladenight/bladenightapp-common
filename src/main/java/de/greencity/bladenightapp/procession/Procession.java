package de.greencity.bladenightapp.procession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.routes.Route;

public class Procession {
	public Procession() {
		init();
	}

	private void init() {
		participants = new ConcurrentHashMap<String, Participant>();
		headMovingPoint = new MovingPoint();
		tailMovingPoint = new MovingPoint();
		route = new Route();
		route.setName("<undefined default route>");
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	// Returned list is read-only!
	public List<Participant> getParticipants() {
		synchronized (participants) {
			return new ArrayList<Participant>(participants.values());
		}
	}

	public int getParticipantCount() {
		return participants.size();
	}

	public void updateParticipant(ParticipantInput input) {
		Participant participant = getOrCreateParticipant(input.getParticipantId());
		getLog().debug("updateParticipant: " + input);
		ParticipantUpdater updater = new ParticipantUpdater(this, participant, input);
		updater.updateParticipant();
		computeProcessionIfTooOld();
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

	public MovingPoint getHead() {
		computeProcessionIfTooOld();
		return headMovingPoint;
	}

	public MovingPoint getTail() {
		computeProcessionIfTooOld();
		return tailMovingPoint;
	}

	public double getLength() {
		computeProcessionIfTooOld();
		return headMovingPoint.getLinearPosition() - tailMovingPoint.getLinearPosition();
	}

	private void computeProcessionIfTooOld() {
		long age = System.currentTimeMillis() - lastComputeTimestamp; 
		getLog().debug("computeProcessionIfTooOld: age="+age);
		if ( age > 1000)
			computeProcession();
	}
	

	public synchronized void computeProcession() {
		synchronized (participants) {
			computeProcessionInternal();
		}
	}
	

	private void computeProcessionInternal() {
		getLog().info("computeProcession");
		lastComputeTimestamp = System.currentTimeMillis(); 

		// TODO check if we need this line. It fails under Linux
		// new GarbageCollector(participants.values()).collect();

		if ( route == null ) {
			getLog().error("computeProcession: no route available");
			return;
		}
		double routeLength = route.getLength();
		if ( routeLength == 0 ) {
			getLog().warn("computeProcession: route has zero length");
			return;
		}

		// TODO move to the application configuration
		int nSegments = 200;
		SegmentedProcession segmentedProcesion = new SegmentedProcession(routeLength, nSegments);

		long startTime = System.currentTimeMillis();

		List<Participant> participantList = new ArrayList<Participant>(participants.values());
		getLog().info("computeProcession: " + participantList.size() + " participants are registered");

		segmentedProcesion.addParticipants(participantList);

		if ( ! segmentedProcesion.computeHeadAndTail() ) {
			getLog().info("computeProcession: could not find the procession position");
			return;
		}

		MovingPoint newHeadMovingPoint = new MovingPoint();
		MovingPoint newTailMovingPoint = new MovingPoint();

		// TODO set also latitude and longitude
		newHeadMovingPoint.update(0, 0, segmentedProcesion.getHeadPosition());
		newTailMovingPoint.update(0, 0, segmentedProcesion.getTailPosition());

		// completeEndMovingPoint(newHeadMovingPoint, headMovingPoint);
		// completeEndMovingPoint(newTailMovingPoint, tailMovingPoint);

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
			//			getLog().info("timestamp1="+newMp.timestamp);
			//			getLog().info("timestamp2="+ lastMp.timestamp);
			double deltaT = newMp.getTimestamp() - lastMp.getTimestamp();
			double computedSpeed = newMp.computeLinearSpeed(lastMp.getLinearPosition(), lastMp.getTimestamp());
			//			getLog().info("computedSpeed="+computedSpeed);
			//			getLog().info("previousP="+lastMp.linearPosition);
			//			getLog().info("currentP="+newMp.linearPosition);
			double ratio;
			// TODO put this setting in the configuration file
			double maxTime = 60000;
			if ( deltaT > maxTime )
				ratio = 0;
			else
				ratio = (maxTime - deltaT) / maxTime; 
			//			getLog().debug("ratio="+ratio);
			newMp.setLinearPosition( (1-ratio) * computedSpeed  + ratio * lastMp.getLinearSpeed() );
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


	private Route route;
	protected MovingPoint headMovingPoint;
	protected MovingPoint tailMovingPoint;

	private Map<String, Participant> participants;

	private static Log log;

	private long lastComputeTimestamp;

	public static void setLog(Log log) {
		Procession.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(Procession.class));
		return log;
	}

}
