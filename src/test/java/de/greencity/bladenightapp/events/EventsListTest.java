package de.greencity.bladenightapp.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.impl.NoOpLog;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;

import de.greencity.bladenightapp.events.Event.EventStatus;
import de.greencity.bladenightapp.persistence.InconsistencyException;
import de.greencity.bladenightapp.persistence.ListPersistor;
import de.greencity.bladenightapp.testutils.Files;

public class EventsListTest {

    @Before
    public void init () {
        //      SimpleLog log = new SimpleLog("EventsListTest");
        //      log.setLevel(0);
        //      EventsList.setLog(log);
        EventList.setLog(new NoOpLog());
    }

    @Test
    public void getNextEventWithNoNextEvent() throws ParseException {
        Event event1 = new Event.Builder().setStartDate("2001-06-01T21:00").build();
        Event event2 = new Event.Builder().setStartDate("2002-06-01T21:00").build();

        EventList manager = new EventList();
        manager.addEvent(event1);
        manager.addEvent(event2);
        Event returnedEvent = manager.getNextEvent();
        assertNull(returnedEvent);
    }

    @Test
    public void getNextEventWithNoOngoingEvent() throws ParseException {
        Event event1 = new Event.Builder().setStartDate("2001-06-01T21:00").build();
        Event event2 = new Event.Builder().setStartDate("2021-06-01T21:00").build();

        EventList manager = new EventList();
        manager.addEvent(event1);
        manager.addEvent(event2);
        Event returnedEvent = manager.getNextEvent();
        assertNotNull(returnedEvent);

        assertEquals(new Event.Builder().setStartDate("2021-06-01T21:00").build(), returnedEvent);

    }

    @Test
    public void getNextEventWithOngoingEvent() throws ParseException {
        EventList eventList = getComplicatedSituation();

        DateTime now = new DateTime();

        Event nextEvent = eventList.getNextEvent();
        assertNotNull(nextEvent);
        assertTrue(nextEvent.getStartDate().isBefore(now));
        assertTrue(nextEvent.getEndDate().isAfter(now));
    }

    @Test
    public void isLive() {
        EventList eventList = getComplicatedSituation();
        Event event1 = eventList.get(0);
        assertTrue(! eventList.isLive(event1));

        Event event2 = eventList.get(1);

        event2.setStatus(EventStatus.CANCELLED);
        assertTrue(! eventList.isLive(event2));

        event2.setStatus(EventStatus.CONFIRMED);
        assertTrue(eventList.isLive(event2));

        event2.setStatus(EventStatus.PENDING);
        assertTrue(! eventList.isLive(event2));

        Event event3 = eventList.get(2);
        assertTrue(! eventList.isLive(event3));
    }

    private EventList getComplicatedSituation() {
        DateTime now = new DateTime();

        DateTime date_1h_ago = now.minus(3600*1000);
        DateTime date_12h_ago = now.minus(12*3600*1000);

        Duration dur_6h = new Duration(6*3600*1000);

        Event event1 = new Event.Builder().setStartDate(date_12h_ago).setDuration(dur_6h).build();
        Event event2 = new Event.Builder().setStartDate(date_1h_ago).setDuration(dur_6h).build();
        Event event3 = new Event.Builder().setStartDate(now.plus(12*3600*1000)).build();

        EventList manager = new EventList();

        manager.addEvent(event1);
        manager.addEvent(event2);
        manager.addEvent(event3);

        return manager;
    }

    @Test
    public void readEventsFromDir() throws IOException, InconsistencyException {
        File dir = FileUtils.toFile(EventList.class.getResource("/de.greencity.bladenightapp.events/set1/"));
        // EventList eventList = EventList.newFromDir(dir);
        EventList eventList = new EventList();
        ListPersistor<Event> persistor = new ListPersistor<Event>(Event.class);
        persistor.setDirectory(dir);
        eventList.setPersistor(persistor);
        eventList.read();
        Event returnedEvent = eventList.getNextEvent();
        assertNotNull(returnedEvent);
        assertEquals( new DateTime("2020-03-03T21:00"), returnedEvent.getStartDate());
        assertEquals(300, returnedEvent.getParticipants());
        assertEquals("route3.gpx", returnedEvent.getRouteName());
        assertEquals(180, returnedEvent.getDuration().getStandardMinutes());
        assertEquals(Event.EventStatus.CONFIRMED, returnedEvent.getStatus());
    }

    @Test
    public void writeEventsToDir() throws IOException, ParseException, InconsistencyException {
        String referenceDate = "2020-02-17T23:00";
        Event event1 = new Event.Builder()
        .setStartDate("2012-02-03T20:00")
        .setDurationInMinutes(60)
        .setRouteName("route1.gpx")
        .setStatus(EventStatus.CANCELLED)
        .build();
        Event event2 = new Event.Builder()
        .setStartDate("2012-02-10T21:00")
        .setDurationInMinutes(120)
        .setRouteName("route2.gpx")
        .setStatus(EventStatus.CONFIRMED)
        .build();
        Event event3 = new Event.Builder()
        .setStartDate(referenceDate)
        .setDurationInMinutes(180)
        .setRouteName("route3.gpx")
        .setStatus(EventStatus.PENDING)
        .build();
        EventList eventList = new EventList();
        eventList.addEvent(event1);
        eventList.addEvent(event2);
        eventList.addEvent(event3);

        File tmpFolder = Files.createTemporaryFolder();

        ListPersistor<Event> persistor = new ListPersistor<Event>(Event.class);
        persistor.setDirectory(tmpFolder);

        File fileToBeDeleted = new File(tmpFolder, "to-be-deleted.per");
        FileUtils.write(fileToBeDeleted, "");

        File fileToBePerserved = new File(tmpFolder, "to-be-preserved");
        FileUtils.write(fileToBePerserved, "");

        assertTrue(fileToBeDeleted.isFile());
        assertTrue(fileToBePerserved.isFile());

        eventList.setPersistor(persistor);
        eventList.write();

        assertTrue(new File(tmpFolder, "2012-02-03.per").isFile());

        assertTrue(fileToBeDeleted.isFile() == false);
        assertTrue(fileToBePerserved.isFile());

        EventList eventList2 = new EventList();
        eventList2.setPersistor(persistor);
        eventList2.read();
        Event returnedEvent = eventList2.getNextEvent();
        assertNotNull(returnedEvent);
        assertEquals(event3, returnedEvent);
    }

    @Test
    public void persistency() throws IOException, InconsistencyException {
        File srcDir = FileUtils.toFile(EventList.class.getResource("/de.greencity.bladenightapp.events/set1/"));
        File tmpFolder = Files.createTemporaryFolder();
        File persistenceFolder = new File(tmpFolder, "copy");
        FileUtils.copyDirectory(srcDir, persistenceFolder);

        ListPersistor<Event> persistor = new ListPersistor<Event>(Event.class);
        persistor.setDirectory(persistenceFolder);

        EventList eventList = new EventList();
        eventList.setPersistor(persistor);
        eventList.read();
        Event returnedEvent = eventList.getNextEvent();
        assertNotNull(returnedEvent);
        assertEquals( new DateTime("2020-03-03T21:00"), returnedEvent.getStartDate());
        assertEquals("route3.gpx", returnedEvent.getRouteName());

        String newRouteName = "Changed route";
        eventList.setNextRoute(newRouteName);
        eventList.setStatusOfNextEvent(EventStatus.CANCELLED);
        eventList.write();

        EventList eventListCheck = new EventList();
        eventListCheck.setPersistor(persistor);
        eventListCheck.read();
        assertEquals(newRouteName, eventListCheck.getNextEvent().getRouteName());

        assertEquals(eventList, eventListCheck);
    }

    @Test
    public void sorting() throws ParseException {
        Event event1 = new Event.Builder()
        .setStartDate("2013-01-01T20:00")
        .setDurationInMinutes(60)
        .setRouteName("route1.gpx")
        .setStatus(EventStatus.CANCELLED)
        .build();
        Event event2 = new Event.Builder()
        .setStartDate("2012-02-10T21:00")
        .setDurationInMinutes(120)
        .setRouteName("route2.gpx")
        .setStatus(EventStatus.CONFIRMED)
        .build();
        Event event3 = new Event.Builder()
        .setStartDate("2014-02-10T21:00")
        .setDurationInMinutes(180)
        .setRouteName("route3.gpx")
        .setStatus(EventStatus.PENDING)
        .build();
        EventList eventList = new EventList();
        eventList.addEvent(event1);
        eventList.addEvent(event2);
        eventList.addEvent(event3);
        eventList.sortByStartDate();
        assertTrue(eventList.get(0).getStartDate().isBefore(eventList.get(1).getStartDate()));
        assertTrue(eventList.get(1).getStartDate().isBefore(eventList.get(2).getStartDate()));

    }
}
