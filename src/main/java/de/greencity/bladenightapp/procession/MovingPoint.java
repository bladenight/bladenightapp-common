package de.greencity.bladenightapp.procession;

import org.apache.commons.lang3.builder.ToStringBuilder;

// Representation of an (optionally) moving point in the procession
public final class MovingPoint {
	public MovingPoint() {
		isOnRoute = false;
		isInProcession = false;
		timestamp = getNewStamp();
	}

	public void setLatLong(double lat, double lon) {
		latitude = lat;
		longitude = lon;
	}

	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}

	public void setLinearPosition(double linearPosition) {
		this.linearPosition = linearPosition;
	}

	public double getLinearPosition() {
		return linearPosition;
	}

	public void update(double latitude, double longitude, double newLinearPosition) {
		long newTimestamp = getNewStamp(); 
		updateLinearSpeed(newLinearPosition, newTimestamp);
		this.timestamp = newTimestamp;
		this.linearPosition = newLinearPosition;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLinearSpeed() {
		return linearSpeed;
	}

	private long getNewStamp() {
		return System.currentTimeMillis();
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isOnRoute() {
		return isOnRoute;
	}

	public void isOnRoute(boolean isOnRoute) {
		this.isOnRoute = isOnRoute;
	}

	public boolean isInProcession() {
		return isInProcession;
	}

	public void isInProcession(boolean isInProcession) {
		this.isInProcession = isInProcession;
	}

	public void setLinearSpeed(double speed) {
		this.linearSpeed = speed;
	}

	private void updateLinearSpeed(double newLinearPosition, long newTimestamp) {
		linearSpeed = computeLinearSpeed(newLinearPosition, newTimestamp);
	}

	private void updateLinearSpeed(MovingPoint previous) {
		linearSpeed = computeLinearSpeed(previous.getLinearPosition(), previous.getTimestamp());
	}

	public double computeLinearSpeed(double newLinearPosition, long newTimestamp) {
		double deltaT = (newTimestamp - timestamp) / ( 3600.0 * 1000.0); // in hours
		double deltaP = (newLinearPosition - linearPosition) / 1000.0; // in km
		if ( deltaT > 0 )
			return deltaP / deltaT;
		return 0;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public long getAge() {
		return System.currentTimeMillis() - timestamp;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private double latitude;
	private double longitude;

	private boolean isOnRoute;
	private boolean isInProcession;
	private double linearPosition;
	private double linearSpeed;
	private long   timestamp; // in ms

}