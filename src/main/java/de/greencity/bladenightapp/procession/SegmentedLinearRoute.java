package de.greencity.bladenightapp.procession;


public class SegmentedLinearRoute {
    public SegmentedLinearRoute(int nSegments) {
        setNumberOfSegments(nSegments);
    }

    public SegmentedLinearRoute(int nSegments, double routeLength) {
        setNumberOfSegments(nSegments);
        setRouteLength(routeLength);
    }

    public double getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(double routeLength) {
        if ( routeLength <= 0)
            throw new IllegalArgumentException("Invalid routeLength: " + routeLength);
        this.routeLength = routeLength;
    }

    public int getSegmentForLinearPosition(double linearPosition) {
        if ( routeLength <= 0)
            throw new IllegalStateException("Invalid routeLength: " + routeLength);
        int segment = (int)( linearPosition * nSegments / routeLength);
        if ( segment >= nSegments )
            segment = nSegments - 1;
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
        return routeLength / nSegments;
    }

    public void setNumberOfSegments(int nSegments) {
        if ( nSegments <= 0)
            throw new IllegalArgumentException("Invalid nSegments: " + nSegments);
        this.nSegments = nSegments;
    }

    public int getNumberOfSegments() {
        return nSegments;
    }

    private int nSegments;
    private double routeLength;
}
