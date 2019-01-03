package de.greencity.bladenightapp.procession;



public interface ProcessionParticipantsListener {

    static class ParticipantData {
        ParticipantData(double position, double speed, double accuracy) {
            this.position = position;
            this.speed = speed;
            this.accuracy = accuracy;
        }
        final public double position;
        final public double speed;
        final public double accuracy;
    }


    public void updateParticipant(String deviceId, ParticipantData participantData);

    public void removeParticipant(String deviceId);

}
