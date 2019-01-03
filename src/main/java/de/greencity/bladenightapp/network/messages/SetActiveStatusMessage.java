package de.greencity.bladenightapp.network.messages;

import de.greencity.bladenightapp.network.messages.EventMessage.EventStatus;


public class SetActiveStatusMessage extends AdminMessage {

    public SetActiveStatusMessage(EventStatus status, String password) {
        setStatus(status);
        authenticate(password);
    }

    public EventStatus getStatus() {
        return sta;
    }

    public void setStatus(EventStatus sta) {
        this.sta = sta;
    }

    private EventStatus sta;
}
