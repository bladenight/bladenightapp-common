package de.greencity.bladenightapp.procession;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ParticipantInput {
	public ParticipantInput(String participantId, double lat, double lon) {
		this.participantId = participantId;
		this.latitude = lat;
		this.longitude = lon;
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

	private double latitude, longitude;
	private String participantId;
}
