package de.greencity.bladenightapp.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

public class EventsManagerTest {

	@Test
	public void getNextEventWithNoNextEvent() throws ParseException {
		Event event1 = new Event.Builder().setStartDate("2001-06-01T21:00").build();
		Event event2 = new Event.Builder().setStartDate("2002-06-01T21:00").build();

		EventsList manager = new EventsList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		Event returnedEvent = manager.getNextEvent();
		assertNull(returnedEvent);
	}

	@Test
	public void getNextEventWithNoOngoingEvent() throws ParseException {
		Event event1 = new Event.Builder().setStartDate("2001-06-01T21:00").build();
		Event event2 = new Event.Builder().setStartDate("2021-06-01T21:00").build();

		EventsList manager = new EventsList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		Event returnedEvent = manager.getNextEvent();
		assertNotNull(returnedEvent);

		assertEquals(new Event.Builder().setStartDate("2021-06-01T21:00").build(), returnedEvent);		

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

		Event event1 = new Event.Builder().setStartDate(date_12h_ago).setDuration(dur_6h).build();
		Event event2 = new Event.Builder().setStartDate(date_1h_ago).setDuration(dur_6h).build();
		Event event3 = new Event.Builder().setStartDate(now.plus(12*3600*1000)).build();

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
		String referenceDate = "2020-02-17T23:00";
		File tempFile = File.createTempFile("EventsManagerTest-writeEvents", ".json");
		Event event1 = new Event.Builder().setStartDate("2012-02-03T20:00").setDurationInMinutes(60).setRouteName("route1.gpx").build();
		Event event2 = new Event.Builder().setStartDate("2012-02-10T21:00").setDurationInMinutes(120).setRouteName("route2.gpx").build();
		Event event3 = new Event.Builder().setStartDate(referenceDate).setDurationInMinutes(180).setRouteName("route3.gpx").build();
		EventsList manager = new EventsList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		manager.addEvent(event3);
		manager.write(tempFile);

//		System.out.println(tempFile.getAbsolutePath());
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		EventsList manager2 = EventsList.newFromFile(tempFile);
		Event returnedEvent = manager2.getNextEvent();
		assertNotNull(returnedEvent);
		assertEquals(new DateTime(referenceDate), returnedEvent.getStartDate());		
		assertEquals(new DateTime(referenceDate).plus(180*60*1000), returnedEvent.getEndDate());		
		assertEquals(event3, returnedEvent);		
	}
}
