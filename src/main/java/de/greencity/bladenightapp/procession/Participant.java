package de.greencity.bladenightapp.procession;

public class Participant {
	public Participant() {
		lastLifeSign = System.currentTimeMillis();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public MovingPoint getLastKnownPoint() {
		if ( lastKnownPoint == null)
			lastKnownPoint = new MovingPoint();
		return lastKnownPoint;
	}

	public void setLastKnownPoint(MovingPoint lastKnownPoint) {
		setLastLifeSign(System.currentTimeMillis());
		this.lastKnownPoint = lastKnownPoint;
	}

	public long getLastLifeSignAge() {
		long age = System.currentTimeMillis() - lastLifeSign;
		if ( age >=0 )
			return age;
		return 0;
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

	public long getNewTimestamp(){
		return System.currentTimeMillis();
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

	private String deviceId;
	private MovingPoint lastKnownPoint;
	private long lastLifeSign;
}
