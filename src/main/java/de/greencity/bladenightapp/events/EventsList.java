package de.greencity.bladenightapp.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

public class EventsList {

	public EventsList() {
	}

	public Event getNextEvent() {
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

	public void addEvent(Event event) {
		events.add(event);
	}

	public static EventsList newFromFile(File file) throws IOException {
	    String json = FileUtils.readFileToString(file);
		return GsonHelper.getGson().fromJson(json, EventsList.class);
	}

	public void write(File file) throws IOException {
		String json = GsonHelper.getGson().toJson(this);
		FileUtils.writeStringToFile(file, json);
	}

	public int size() {
		return events.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	protected List<Event> events = new ArrayList<Event>();
	// protected Map<DateTime, Event> events2 = new HashMap<DateTime, Event>(); 

}
