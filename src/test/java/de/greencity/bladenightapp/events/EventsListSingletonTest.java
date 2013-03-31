package de.greencity.bladenightapp.events;

import static org.junit.Assert.*;

import org.junit.Test;

public class EventsListSingletonTest {
	@Test
	public void test() {
		EventList list = new EventList();
		EventsListSingleton.setInstance(list);
		assertTrue(EventsListSingleton.getInstance() == list);
	}
}
