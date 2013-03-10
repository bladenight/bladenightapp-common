package de.greencity.bladenightapp.events;

import de.greencity.bladenightapp.events.EventsList;

public class EventsListSingleton {

	public static EventsList getInstance() {
		return instance;
	}

	public static void setInstance(EventsList instance) {
		EventsListSingleton.instance = instance;
	}

	private static EventsList instance;

}
