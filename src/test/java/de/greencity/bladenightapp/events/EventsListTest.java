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

public class EventsListTest {

	@Before
	public void init () {
		//		SimpleLog log = new SimpleLog("EventsListTest");
		//		log.setLevel(0);
		//		EventsList.setLog(log);
		EventsList.setLog(new NoOpLog());
	}

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
	public void readEventsFromDir() throws IOException {
		File dir = FileUtils.toFile(EventsList.class.getResource("/de.greencity.bladenightapp.events/set1/"));
		EventsList manager = EventsList.newFromDir(dir);
		Event returnedEvent = manager.getNextEvent();
		assertNotNull(returnedEvent);
		assertEquals( new DateTime("2020-03-03T21:00"), returnedEvent.getStartDate());
		assertEquals(300, returnedEvent.getParticipants());
		assertEquals("route3.gpx", returnedEvent.getRouteName());
		assertEquals(180, returnedEvent.getDuration().getStandardMinutes());
		assertEquals(Event.EventStatus.CONFIRMED, returnedEvent.getStatus());
	}

	@Test
	public void writeEventsToDir() throws IOException, ParseException {
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
		EventsList manager = new EventsList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		manager.addEvent(event3);

		File tmpFolder = createTemporaryFolder();
		File fileToBeDeleted = new File(tmpFolder, "to-be-deleted." + EventsList.EVENT_FILE_EXTENSION);
		FileUtils.write(fileToBeDeleted, "");

		assertTrue(fileToBeDeleted.isFile());

		manager.writeToDir(tmpFolder);

		assertTrue(new File(tmpFolder, "2012-02-03." + EventsList.EVENT_FILE_EXTENSION).isFile());

		assertTrue(fileToBeDeleted.isFile() == false);

		EventsList manager2 = EventsList.newFromDir(tmpFolder);
		Event returnedEvent = manager2.getNextEvent();
		assertNotNull(returnedEvent);
		assertEquals(event3, returnedEvent);		
	}

	public File createTemporaryFolder() throws IOException  {
		File file = File.createTempFile("tmpfolder", ".d");
		file.delete();
		file.mkdir();
		assertTrue(file.exists());
		assertTrue(file.isDirectory());
		return file;
	}

}
