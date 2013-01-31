package de.greencity.bladenightapp.events;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

public class EventsManagerTest {

	@Test
	public void getNextEventWithNoNextEvent() throws ParseException {
		Event event1 = new Event.Builder().setStart("2001-06-01T21:00").build();
		Event event2 = new Event.Builder().setStart("2002-06-01T21:00").build();

		EventsList manager = new EventsList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		Event returnedEvent = manager.getNextEvent();
		assertNull(returnedEvent);
	}

	@Test
	public void getNextEventWithNoOngoingEvent() throws ParseException {
		Event event1 = new Event.Builder().setStart("2001-06-01T21:00").build();
		Event event2 = new Event.Builder().setStart("2021-06-01T21:00").build();

		EventsList manager = new EventsList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		Event returnedEvent = manager.getNextEvent();
		assertNotNull(returnedEvent);

		assertEquals(new Event.Builder().setStart("2021-06-01T21:00").build(), returnedEvent);		

	}

	@Test
	public void getNextEventWithOngoingEvent() throws ParseException {
		EventsList manager = getComplicatedSituation();

		DateTime now = new DateTime();

		Event returnedEvent = manager.getNextEvent();
		assertNotNull(returnedEvent);
		assertTrue(returnedEvent.getStartDate().isBefore(now));		
		assertTrue(returnedEvent.getEndDate().isAfter(now));		
	}

	private EventsList getComplicatedSituation() {
		DateTime now = new DateTime();

		DateTime date_1h_ago = now.minus(3600*1000); 
		DateTime date_12h_ago = now.minus(12*3600*1000);

		Duration dur_6h = new Duration(6*3600*1000);

		Event event1 = new Event.Builder().setStart(date_12h_ago).setDuration(dur_6h).build();
		Event event2 = new Event.Builder().setStart(date_1h_ago).setDuration(dur_6h).build();
		Event event3 = new Event.Builder().setStart(now.plus(12*3600*1000)).build();

		EventsList manager = new EventsList();

		manager.addEvent(event1);
		manager.addEvent(event2);
		manager.addEvent(event3);

		return manager;
	}

	@Test
	public void readEvents() throws IOException {
        File file = FileUtils.toFile(EventsList.class.getResource("/de.greencity.bladenightapp.events/example.json"));
		EventsList manager = EventsList.newFromFile(file);
		Event returnedEvent = manager.getNextEvent();
		assertNotNull(returnedEvent);
		assertEquals(300, returnedEvent.getParticipants());
		assertEquals("route3.gpx", returnedEvent.getRouteName());
		assertEquals(180, returnedEvent.getDuration().getStandardMinutes());
	}

	@Test
	public void writeEvents() throws IOException, ParseException {
		String referenceDate = "2013-02-17T23:00";
		File tempFile = File.createTempFile("EventsManagerTest-writeEvents", ".json");
		Event event1 = new Event.Builder().setStart("2012-02-03T20:00").setMinutes(60).setRouteName("route1.gpx").build();
		Event event2 = new Event.Builder().setStart("2012-02-10T21:00").setMinutes(120).setRouteName("route2.gpx").build();
		Event event3 = new Event.Builder().setStart(referenceDate).setMinutes(180).setRouteName("route3.gpx").build();
		EventsList manager = new EventsList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		manager.addEvent(event3);
		manager.write(tempFile);

		EventsList manager2 = EventsList.newFromFile(tempFile);
		Event returnedEvent = manager2.getNextEvent();
		assertNotNull(returnedEvent);
		assertEquals(new DateTime(referenceDate), returnedEvent.getStartDate());		
		assertEquals(new DateTime("2013-02-17T23:00").plus(180*60*1000), returnedEvent.getEndDate());		
		assertEquals(event3, returnedEvent);		
	}
}
