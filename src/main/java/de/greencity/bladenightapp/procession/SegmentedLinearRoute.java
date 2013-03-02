package de.greencity.bladenightapp.procession;


public class SegmentedLinearRoute {
	SegmentedLinearRoute(int nSegment) {
		this.nSegment = nSegment;
		if ( nSegment <= 0)
			throw new IllegalArgumentException("Invalid nSegment: " + nSegment);
	}

	public double getRouteLength() {
		return routeLength;
	}

	public void setRouteLength(double routeLength) {
		this.routeLength = routeLength;
	}

	public int getSegmentForLinearPosition(double linearPosition) {
		if ( routeLength <= 0)
			throw new IllegalStateException("Invalid routeLength: " + routeLength);
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

	public int getNumberOfSegments() {
		return nSegment;
	}

	private int nSegment;
	private double routeLength;
}
