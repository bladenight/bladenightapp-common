package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.greencity.bladenightapp.procession.MovingPoint;

public class NetMovingPoint {
	// We use integers instead of double's for distances to save bandwidth
	public long pos;
	public long spd;
	private long eta;
	public boolean ior;
	public boolean iip;
	public double lat;
	public double lon;
	
	public NetMovingPoint() {
	}

	public NetMovingPoint(long linearPosition, long linearSpeed) {
		setPosition(linearPosition);
		isOnRoute(true);
		setSpeed(linearSpeed);
	}

	public NetMovingPoint(long linearPosition, long linearSpeed, boolean isInProcession) {
		setPosition(linearPosition);
		isOnRoute(true);
		setSpeed(linearSpeed);
		isInProcession(isInProcession);
	}

	public NetMovingPoint(MovingPoint mp) {
		copyFrom(mp);
	}
	
	public void copyFrom(MovingPoint mp) {
		setPosition((long)mp.getLinearPosition());
		setSpeed((long)mp.getLinearSpeed());
		setLatitude(mp.getLatitude());
		setLongitude(mp.getLongitude());
		isOnRoute(mp.isOnRoute());
		isInProcession(mp.isInProcession());
	}

	public long getPosition() {
		return pos;
	}

	public void setPosition(long pos) {
		this.pos = pos;
	}

	public long getSpeed() {
		return spd;
	}
	
	public void setSpeed(long spd) {
		this.spd = spd;
	}
	
	public void isOnRoute(boolean isOnRoute) {
		this.ior = isOnRoute;
	}

	public boolean isOnRoute() {
		return this.ior;
	}

	public void isInProcession(boolean isInProcession) {
		this.iip = isInProcession;
	}

	public boolean isInProcession() {
		return this.iip;
	}

	public double getLatitude() {
		return lat;
	}

	public void setLatitude(double lat) {
		this.lat = lat;
	}

	public double getLongitude() {
		return lon;
	}

	public long getEstimatedTimeToArrival() {
		return eta;
	}

	public void setEstimatedTimeToArrival(long eta) {
		this.eta = eta;
	}

	public void setLongitude(double lon) {
		this.lon = lon;
	}
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
