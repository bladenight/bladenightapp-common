package de.greencity.bladenightapp.procession;

public interface ProcessionParticipantsListener {
	public void updateParticipant(String deviceId, double position, double speed);

	public void removeParticipant(String deviceId);
}
