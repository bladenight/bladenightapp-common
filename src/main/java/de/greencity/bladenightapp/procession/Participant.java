package de.greencity.bladenightapp.procession;

import de.greencity.bladenightapp.time.Clock;
import de.greencity.bladenightapp.time.SystemClock;

public class Participant {
	public Participant() {
		lastLifeSign = clock.currentTimeMillis();
		lastKnownPoint = new MovingPoint();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	MovingPoint getLastKnownPoint() {
		return lastKnownPoint;
	}

	public void setLastKnownPoint(MovingPoint lastKnownPoint) {
		setLastLifeSign(clock.currentTimeMillis());
		this.lastKnownPoint = lastKnownPoint;
	}

	public long getLastLifeSign() {
		return lastLifeSign;
	}

	public void setLastLifeSign(long lastLifeSign) {
		this.lastLifeSign = lastLifeSign;
	}

	public double getLinearPosition() {
		return lastKnownPoint.getLinearPosition();
	}

	public double getLatitude() {
		return lastKnownPoint.getLatitude();
	}

	public double getLongitude() {
		return lastKnownPoint.getLongitude();
	}

	public void isOnRoute(boolean isOnRoute) {
		lastKnownPoint.isOnRoute(isOnRoute);
	}

	public boolean isOnRoute() {
		return lastKnownPoint.isOnRoute();
	}

	public double getLinearSpeed() {
		return lastKnownPoint.getLinearSpeed();
	}

	private String deviceId;
	private MovingPoint lastKnownPoint;
	private long lastLifeSign;
	private Clock clock = new SystemClock();
}
