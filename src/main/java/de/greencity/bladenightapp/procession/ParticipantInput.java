package de.greencity.bladenightapp.procession;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ParticipantInput {
	public ParticipantInput(String participantId, boolean isParticipating, double lat, double lon) {
		this.participantId = participantId;
		this.isParticipating = isParticipating;
		this.latitude = lat;
		this.longitude = lon;
	}

	public ParticipantInput(String participantId, boolean isParticipating, double lat, double lon, int accuracy) {
		this.participantId = participantId;
		this.isParticipating = isParticipating;
		this.latitude = lat;
		this.longitude = lon;
		this.accuracy = accuracy;
	}

	public String getParticipantId() {
		return participantId;
	}

	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void isParticipating(boolean isParticipating) {
		this.isParticipating = isParticipating;
	}

	public boolean isParticipating() {
		return isParticipating;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	private double latitude, longitude;
	private String participantId;
	private boolean isParticipating;
	private double accuracy;
}
