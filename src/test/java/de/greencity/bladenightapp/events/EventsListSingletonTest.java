package de.greencity.bladenightapp.events;

import static org.junit.Assert.*;

import org.junit.Test;

public class EventsListSingletonTest {
	@Test
	public void test() {
		EventsList list = new EventsList();
		EventsListSingleton.setEventsList(list);
		assertTrue(EventsListSingleton.getInstance() == list);
	}
}
