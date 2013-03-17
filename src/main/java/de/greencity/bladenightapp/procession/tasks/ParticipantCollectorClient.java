package de.greencity.bladenightapp.procession.tasks;

import java.util.List;

import de.greencity.bladenightapp.procession.Participant;

public interface ParticipantCollectorClient {
	public List<Participant> getParticipants();
	public void removeParticipant(String deviceId);
}
