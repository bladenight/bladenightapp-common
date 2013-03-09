package de.greencity.bladenightapp.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.BeforeClass;
import org.junit.Test;

public class EventTest {
	Event event1;
	Event event2;
	static DateTime now;
	static Duration dur_6h;
	static Duration dur_1s;

	@BeforeClass
	static public void initClass() {
		now = new DateTime();
		dur_6h = new Duration(6*3600*1000);
		dur_1s = new Duration(1);
	}
	
	@Test
	public void simpleEqualEvents() {
		event1 = new Event.Builder().setStartDate(now).build();
		event2 = new Event.Builder().setStartDate(now).build();
		assertEquals(event1, event2);
	}

	@Test
	public void simpleNonEqualEvents() {
		event1 = new Event.Builder().setStartDate(now).build();
		event2 = new Event.Builder().setStartDate(now.plus(dur_1s)).build();
		assertFalse(event1.equals(event2));
	}


	@Test
	public void routeShallBeConsidered() {
		event1 = new Event.Builder().setStartDate(now).build();
		event2 = new Event.Builder().setStartDate(now).setRouteName("test").build();
		assertFalse(event1.equals(event2));
	}

	@Test
	public void durationShallBeConsidered() {
		event1 = new Event.Builder().setStartDate(now).build();
		event2 = new Event.Builder().setStartDate(now).setDuration(dur_6h).build();
		assertFalse(event1.equals(event2));
	}

	@Test
	public void defaultStatusIsPending() {
		event1 = new Event.Builder().setStartDate(now).build();
		assertEquals(Event.EventStatus.PENDING, event1.getStatus());
		
		event1.setStatus(Event.EventStatus.CANCELLED);
		assertEquals(Event.EventStatus.CANCELLED, event1.getStatus());
	}
}
