package de.greencity.bladenightapp.events;

import de.greencity.bladenightapp.events.EventList;

public class EventsListSingleton {

	public static EventList getInstance() {
		return instance;
	}

	public static void setInstance(EventList instance) {
		EventsListSingleton.instance = instance;
	}

	private static EventList instance;

}
