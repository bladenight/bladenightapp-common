package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GpsInfo {
	public GpsInfo(String deviceId, long lat, long lon) {
		coo = new LatLong(lat, lon);
		this.deviceId = deviceId;
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
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private LatLong coo;
	private String deviceId;
}
