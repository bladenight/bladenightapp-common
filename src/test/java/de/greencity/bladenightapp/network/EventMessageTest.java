package de.greencity.bladenightapp.network;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import de.greencity.bladenightapp.events.Event;
import de.greencity.bladenightapp.network.messages.EventMessage;

public class EventMessageTest {
    @Test
    public void toMessage() {
        String routeName = "some route";
        DateTime date = new DateTime("2013-02-03T21:00");
        Event event = new Event.Builder()
            .setStartDate(date)
            .setDurationInMinutes(120)
            .setParticipants(1000)
            .setRouteName(routeName)
            .build();
        EventMessage msg = new EventMessage(event);
        assertEquals("2013-02-03T21:00", msg.sta);
        assertEquals(120, msg.dur);
        assertEquals(routeName, msg.rou);
        assertEquals(1000, msg.par);
        assertEquals(EventMessage.EventStatus.PEN, msg.sts);
    }

    @Test
    public void fromMessage() {
        String routeName = "some route";
        EventMessage msg = new EventMessage();
        msg.sta = "2013-02-03T21:00";
        msg.par = 1000;
        msg.rou = routeName;
        msg.dur = 120;
        msg.sts = EventMessage.EventStatus.CON;

        Event e = msg.toEvent();

        assertEquals(e.getStartDate(), new DateTime("2013-02-03T21:00"));
        assertEquals(e.getEndDate(), new DateTime("2013-02-03T23:00"));
        assertEquals(e.getDuration(), new Duration(120*60*1000));
        assertEquals(e.getParticipants(), 1000);
        assertEquals(e.getRouteName(), routeName);
        assertEquals(Event.EventStatus.CONFIRMED, e.getStatus());
    }
}
