package de.greencity.bladenightapp.procession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.procession.ProcessionParticipantsListener.ParticipantData;
import de.greencity.bladenightapp.procession.Statistics.Segment;
import de.greencity.bladenightapp.procession.tasks.ComputeSchedulerClient;
import de.greencity.bladenightapp.procession.tasks.ParticipantCollectorClient;
import de.greencity.bladenightapp.routes.Route;
import de.greencity.bladenightapp.routes.Route.ProjectedLocation;
import de.greencity.bladenightapp.time.Clock;
import de.greencity.bladenightapp.time.SystemClock;

public class Procession implements ComputeSchedulerClient, ParticipantCollectorClient {
    public Procession(Clock clock) {
        this.clock = clock;
        init();
    }

    public Procession() {
        init();
    }

    private void init() {
        trackedParticipants = new ConcurrentHashMap<String, Participant>();
        headMovingPoint = new MovingPoint();
        tailMovingPoint = new MovingPoint();
        route = new Route();
        route.setName("<undefined default route>");

        initComputers();
    }

    private void initComputers() {
        // TODO move to the application configuration
        int nSegments = 200;
        headAndTailComputer = new HeadAndTailComputer(nSegments);
        travelTimeComputer = new TravelTimeComputer(nSegments);
        travelTimeComputer.setClock(clock);
        double routeLength = 0.0;
        if ( route != null )
            routeLength = route.getLength();
        if ( routeLength <= 0.0) {
            routeLength = 9999.0;
        }
        headAndTailComputer.setRouteLength(routeLength);
        travelTimeComputer.setRouteLength(routeLength);
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        if ( route == null ) {
            getLog().error("setRoute: refusing to set a null route");
            return;
        }
        this.route = route;
        initComputers();
    }

    // Returned list is read-only!
    public List<Participant> getParticipants() {
        return new ArrayList<Participant>(trackedParticipants.values());
    }

    public void removeParticipant(String deviceId) {
        getLog().info("Removing participant " + deviceId);
        trackedParticipants.remove(deviceId);
        headAndTailComputer.removeParticipant(deviceId);
        travelTimeComputer.removeParticipant(deviceId);
    }

    public int getParticipantCount() {
        return trackedParticipants.size();
    }

    public int getParticipantsOnRoute() {
        int count = 0;
        for ( Participant p : trackedParticipants.values()) {
            if ( p.isOnRoute() ) {
                count++;
            }
        }
        return count;
    }

    public synchronized Participant updateParticipant(ParticipantInput participantInput) {
        getLog().debug("updateParticipant: " + participantInput);

        String participantId = participantInput.getParticipantId();

        Participant participant;

        // TODO redesign the situation where a participant is not participating (sic)
        if ( participantInput.isParticipating() ) {
            participant = trackedParticipants.get(participantId);
            if ( participant == null ) {
                participant = getOrCreateParticipant(participantId);
            }
        }
        else {
            // we create a temporary participant and we don't add it to the list of tracked participants
            participant = newParticipant(participantId);
        }

        List<ProjectedLocation> potentialLocations = route.projectPosition(participantInput.getLatitude(), participantInput.getLongitude());

        ParticipantUpdater updater = new ParticipantUpdater.Builder().
                setProcessionEnds(getTailPosition(), getHeadPosition()).
                setParticipantInput(participantInput).
                setParticipant(participant).
                setPotentialLocations(potentialLocations).
                setRouteLength(route.getLength()).
                setClock(clock).
                build();

        updater.updateParticipant();

        if ( participant.isOnRoute() && participantInput.isParticipating() ) {
            ParticipantData participantData = new ParticipantData(participant.getLinearPosition(), participant.getLinearSpeed(), participantInput.getAccuracy());
            headAndTailComputer.updateParticipant(participantId, participantData);
            travelTimeComputer.updateParticipant(participantId, participantData);
        }
        else {
            headAndTailComputer.removeParticipant(participantId);
            travelTimeComputer.removeParticipant(participantId);
        }

        return participant;
    }

    // Result is read-only
    public Participant getParticipant(String id) {
        return trackedParticipants.get(id);
    }

    private Participant getOrCreateParticipant(String id) {
        Participant p = trackedParticipants.get(id);
        if ( p == null ) {
            p = newParticipant(id);
            trackedParticipants.put(id, p);
        }
        return p;
    }

    private Participant newParticipant(String id) {
        Participant p = new Participant();
        p.setDeviceId(id);
        return p;
    }

    public boolean isParticipantOnRoute(String deviceId) {
        Participant participant = getParticipant(deviceId);
        if ( participant == null)
            return false;
        return participant.isOnRoute();
    }


    public MovingPoint getHead() {
        computeIfTooOld();
        return headMovingPoint;
    }

    public MovingPoint getTail() {
        computeIfTooOld();
        return tailMovingPoint;
    }

    public double getLength() {
        computeIfTooOld();
        return headMovingPoint.getLinearPosition() - tailMovingPoint.getLinearPosition();
    }

    public double getHeadPosition() {
        return getHead().getLinearPosition();
    }

    public double getTailPosition() {
        return getTail().getLinearPosition();
    }

    @Override
    public synchronized void compute() {
        getLog().debug("compute");

        lastComputeTime = clock.currentTimeMillis();

        if ( route == null ) {
            getLog().error("compute: no route available");
            return;
        }
        double routeLength = route.getLength();
        if ( routeLength == 0 ) {
            getLog().warn("compute: route has zero length");
            return;
        }

        long startTime = System.currentTimeMillis();

        List<Participant> participantList = new ArrayList<Participant>(trackedParticipants.values());
        getLog().debug("compute: " + participantList.size() + " participants are registered");

        if ( ! headAndTailComputer.compute() ) {
            getLog().debug("compute: could not find the procession position");
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

        getLog().info("headMovingPoint="+headMovingPoint);
        getLog().info("tailMovingPoint="+tailMovingPoint);

        travelTimeComputer.computeTravelTimeForAllSegments(0.75);

        computeStatistics();

        long endTime = System.currentTimeMillis();

        getLog().debug("compute: compute time: " + (endTime-startTime)+"ms");
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
        Route.LatLong latLong = route.convertLinearPositionToLatLong(newMp.getLinearPosition());
        newMp.setLatLong(latLong.lat, latLong.lon);
    }

    private void computeIfTooOld() {
        if ( isMaxComputeAgeEnabled() && isMaxComputeAgeReached() ) {
            getLog().info("Triggering compute automatically");
            compute();
        }
    }

    private boolean isMaxComputeAgeEnabled() {
        return maxComputeAge >= 0;
    }

    private boolean isMaxComputeAgeReached() {
        return lastComputeTime < 0 || maxComputeAge == 0 || System.currentTimeMillis() - lastComputeTime > lastComputeTime;
    }

    public boolean isValid() {
        return getHead().isOnRoute() &&
                getTail().isOnRoute() &&
                getHead().getLinearPosition() >= getTail().getLinearPosition();
    }

    public double getUpdateSmoothingFactor() {
        return updateSmoothingFactor;
    }

    public double evaluateTravelTimeBetween(double position1, double position2) {
        computeIfTooOld();
        double result = travelTimeComputer.evaluateTravelTimeBetween(position1, position2);
        getLog().debug("evaluateTravelTimeBetween("+position1+","+position2+")");
        return result;
    }

    public long getMaxComputeAge() {
        return maxComputeAge;
    }

    /*** If set, trigger a compute if the last compute is older than specified.
     * Disabled by default.
     * Set to 0 for systematic updates
     * Set to -1 to disable again.
     */
    public void setMaxComputeAge(long maxComputeAge) {
        this.maxComputeAge = maxComputeAge;
    }


    /***
     * Smoothen jumps of the head and the tail
     * 0.0  : no smoothing
     * 0.99 : unreasonable smoothing
     */
    public void setUpdateSmoothingFactor(double updateSmoothingFactor) {
        this.updateSmoothingFactor = updateSmoothingFactor;
    }

    public double getProcessionGreediness() {
        return headAndTailComputer.getProcessionGreediness();
    }

    public void setProcessionGreediness(double processionGreediness) {
        headAndTailComputer.setProcessionGreediness(processionGreediness);
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void computeStatistics() {
        int nSegments = travelTimeComputer.getNumberOfSegments();
        SegmentedLinearRoute segmentedLinearRoute = new SegmentedLinearRoute(nSegments, route.getLength());
        double segmentLength = segmentedLinearRoute.getSegmentLength();
        double sumSpeed = 0;
        int nSegmentsWithSpeed = 0;
        Statistics statistics = new Statistics();
        statistics.segments = new Segment[nSegments];
        for(int segment=0 ; segment < nSegments ; segment++) {
            statistics.segments[segment] = new Segment();
            //          System.out.println("segment="+segment);
            //          System.out.println("segmentLength="+segmentLength);
            //          System.out.println("time="+travelTimeComputer.getTravelTimeForSegment(segment));
            double speed = 3600.0 * (segmentLength / travelTimeComputer.getTravelTimeForSegment(segment));
            statistics.segments[segment].speed = speed;
            if ( ! Double.isNaN(speed)  ) {
                sumSpeed += speed;
                nSegmentsWithSpeed ++;
            }
        }
        if ( nSegmentsWithSpeed > 0 )
            statistics.averageSpeed = (sumSpeed / nSegmentsWithSpeed);
        for(Participant p : trackedParticipants.values()) {
            int segment = segmentedLinearRoute.getSegmentForLinearPosition(p.getLinearPosition());
            statistics.segments[segment].nParticipants++;
        }
        this.statistics = statistics;
    }


    private Route               route;
    private MovingPoint         headMovingPoint;
    private MovingPoint         tailMovingPoint;
    private HeadAndTailComputer headAndTailComputer;
    private TravelTimeComputer  travelTimeComputer;
    private Statistics          statistics;
    private long                maxComputeAge = -1;
    private long                lastComputeTime = -1;
    private Clock               clock = new SystemClock();

    protected double updateSmoothingFactor = 0.0;


    private Map<String, Participant> trackedParticipants;

    private static Log log;

    public static void setLog(Log log) {
        Procession.log = log;
    }

    protected static Log getLog() {
        if (log == null)
            setLog(LogFactory.getLog(Procession.class));
        return log;
    }

}
