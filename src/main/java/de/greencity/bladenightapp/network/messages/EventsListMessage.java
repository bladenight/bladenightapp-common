package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.events.Event;
import de.greencity.bladenightapp.events.EventsList;

public class EventsListMessage {
	public EventMessage[] evt;

	public static EventsListMessage newFromEventsList(EventsList list) {
		EventsListMessage message = new EventsListMessage();
		message.copyFromEventsList(list);
		return message;
	}

	public void copyFromEventsList(EventsList list) {
		evt = new EventMessage[list.size()];
		int i = 0;
		for (Event e : list) {
			evt[i++] = EventMessage.newFromEvent(e);
		}
	}

	public EventsList convertToEventsList() {
		EventsList list = new EventsList();
		for (int i=0; i<evt.length; i++) {
			list.addEvent(evt[i].toEvent());
		}
		return list;
	}

	public EventMessage[] getEvents() {
		return evt;
	}

	public void setEvents(EventMessage[] evt) {
		this.evt = evt;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	private static Log log;

	public static void setLog(Log log) {
		EventsListMessage.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(EventsListMessage.class));
		return log;
	}
}