package de.greencity.bladenightapp.events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import de.greencity.bladenightapp.events.Event.EventStatus;
import de.greencity.bladenightapp.persistence.ListPersistor;

public class EventList implements Iterable<Event> {

	public EventList() {
		events = new ArrayList<Event>();
	}

	public void read() throws IOException {
		persistor.setList(events);
		persistor.setGson(EventGsonHelper.getGson());
		persistor.read();
	}

	public void write() throws IOException {
		persistor.setList(events);
		persistor.setGson(EventGsonHelper.getGson());
		persistor.write();
	}
	
	public void setPersistor(ListPersistor<Event> persistor) {
		this.persistor = persistor;
	}


	public Event getActiveEvent() {
		Event nextEvent = null;
		DateTime now = new DateTime();
		for ( Event event : events ) {
			if ( now.isBefore(event.getEndDate()) ) {
				if ( nextEvent == null || event.getStartDate().isBefore(nextEvent.getStartDate()) )
					nextEvent = event;
			}

		}
		return nextEvent;
	}
	
	public void setActiveRoute(String routeName) {
		Event event = getActiveEvent();
		if ( event == null ) {
			getLog().error("setActiveRoute: No current event found");
			return;
		}
		event.setRouteName(routeName);
	}

	public void setActiveStatus(EventStatus newStatus) {
		Event event = getActiveEvent();
		if ( event == null ) {
			getLog().error("setActiveStatus: No current event found");
			return;
		}
		event.setStatus(newStatus);
	}


	public void addEvent(Event event) {
		events.add(event);
	}

	public Event get(int pos) {
		return events.get(pos);
	}

	public int size() {
		return events.size();
	}
	
	public int indexOf(Event event) {
		return events.indexOf(event);
	}

	@Override
	public Iterator<Event> iterator() {
		return events.iterator();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static Log log;

	public static void setLog(Log log) {
		EventList.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(EventList.class));
		return log;
	}

	protected List<Event> events;
	private ListPersistor<Event> persistor;

}