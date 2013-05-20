package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.events.Event;
import de.greencity.bladenightapp.events.EventList;

public class EventListMessage {
	public EventMessage[] evt = new EventMessage[0];

	public static EventListMessage newFromEventsList(EventList list) {
		EventListMessage message = new EventListMessage();
		message.copyFromEventsList(list);
		return message;
	}

	public void copyFromEventsList(EventList list) {
		evt = new EventMessage[list.size()];
		int i = 0;
		for (Event e : list) {
			evt[i++] = EventMessage.newFromEvent(e);
		}
	}

	public EventList convertToEventsList() {
		EventList list = new EventList();
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
		EventListMessage.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(EventListMessage.class));
		return log;
	}

	public int size() {
		return evt.length;
	}

	public EventMessage get(int position) {
		return evt[position];
	}
}
