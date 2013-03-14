package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GpsInfo {
	public GpsInfo(String deviceId, boolean isParticipating, double lat, double lon) {
		coo = new LatLong(lat, lon);
		this.par = isParticipating;
		this.did = deviceId;
	}

	public GpsInfo() {
		coo = new LatLong();
	}
	
	public double getLatitude() {
		return coo.getLatitude();
	}

	public void setLatitude(double latitude) {
		coo.setLatitude(latitude);
	}

	public double getLongitude() {
		return coo.getLongitude();
	}

	public void setLongitude(double longitude) {
		coo.setLongitude(longitude);
	}

	public String getDeviceId() {
		return did;
	}

	public void setDeviceId(String deviceId) {
		this.did = deviceId;
	}

	public boolean isParticipating() {
		return par;
	}
	
	public void isParticipating(boolean isParticipating) {
		this.par = isParticipating;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public LatLong coo;
	public String did;
	public boolean par;
}
