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
		EventList.setLog(new NoOpLog());
	}

	@Test
	public void getNextEventWithNoNextEvent() throws ParseException {
		Event event1 = new Event.Builder().setStartDate("2001-06-01T21:00").build();
		Event event2 = new Event.Builder().setStartDate("2002-06-01T21:00").build();

		EventList manager = new EventList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		Event returnedEvent = manager.getActiveEvent();
		assertNull(returnedEvent);
	}

	@Test
	public void getNextEventWithNoOngoingEvent() throws ParseException {
		Event event1 = new Event.Builder().setStartDate("2001-06-01T21:00").build();
		Event event2 = new Event.Builder().setStartDate("2021-06-01T21:00").build();

		EventList manager = new EventList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		Event returnedEvent = manager.getActiveEvent();
		assertNotNull(returnedEvent);

		assertEquals(new Event.Builder().setStartDate("2021-06-01T21:00").build(), returnedEvent);		

	}

	@Test
	public void getNextEventWithOngoingEvent() throws ParseException {
		EventList manager = getComplicatedSituation();

		DateTime now = new DateTime();

		Event returnedEvent = manager.getActiveEvent();
		assertNotNull(returnedEvent);
		assertTrue(returnedEvent.getStartDate().isBefore(now));		
		assertTrue(returnedEvent.getEndDate().isAfter(now));		
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
	public void readEventsFromDir() throws IOException {
		File dir = FileUtils.toFile(EventList.class.getResource("/de.greencity.bladenightapp.events/set1/"));
		EventList eventList = EventList.newFromDir(dir);
		Event returnedEvent = eventList.getActiveEvent();
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
		EventList manager = new EventList();
		manager.addEvent(event1);
		manager.addEvent(event2);
		manager.addEvent(event3);

		File tmpFolder = createTemporaryFolder();
		File fileToBeDeleted = new File(tmpFolder, "to-be-deleted." + EventList.EVENT_FILE_EXTENSION);
		FileUtils.write(fileToBeDeleted, "");

		assertTrue(fileToBeDeleted.isFile());

		manager.writeToDir(tmpFolder);

		assertTrue(new File(tmpFolder, "2012-02-03." + EventList.EVENT_FILE_EXTENSION).isFile());

		assertTrue(fileToBeDeleted.isFile() == false);

		EventList manager2 = EventList.newFromDir(tmpFolder);
		Event returnedEvent = manager2.getActiveEvent();
		assertNotNull(returnedEvent);
		assertEquals(event3, returnedEvent);		
	}

	@Test
	public void persistency() throws IOException {
		File srcDir = FileUtils.toFile(EventList.class.getResource("/de.greencity.bladenightapp.events/set1/"));
		File tmpFolder = createTemporaryFolder();
		File persistenceFolder = new File(tmpFolder, "copy");
		FileUtils.copyDirectory(srcDir, persistenceFolder);
		
		EventList eventList = EventList.newFromDir(persistenceFolder);
		Event returnedEvent = eventList.getActiveEvent();
		assertNotNull(returnedEvent);
		assertEquals( new DateTime("2020-03-03T21:00"), returnedEvent.getStartDate());
		assertEquals("route3.gpx", returnedEvent.getRouteName());
		
		String newRouteName = "Changed route";
		eventList.setActiveRoute(newRouteName);
		eventList.setActiveStatus(EventStatus.CANCELLED);
		eventList.writeToDir();
		
		EventList eventListCheck = EventList.newFromDir(persistenceFolder);
		assertEquals(newRouteName, eventListCheck.getActiveEvent().getRouteName());
		
		assertEquals(eventList, eventListCheck);
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
