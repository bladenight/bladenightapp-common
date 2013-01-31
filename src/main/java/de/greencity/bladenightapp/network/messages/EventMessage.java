package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.greencity.bladenightapp.events.Event;

public class EventMessage {
	public String 	sta; 	// start time: "yyyy-MM-dd'T'HH:mm"
	public long 	dur; 	// duration in minutes
	public String 	rou; 	// route name
	public int 		par;	// number of participants

	public EventMessage() {
	}
	public EventMessage(Event e) {
		fromEvent(e);
	}

	void fromEvent(Event e) {
		sta = dateFormatter.print(e.getStartDate());
		dur = e.getDuration().getStandardMinutes();
		rou = e.getRouteName();
		par = e.getParticipants();
	}

	public Event toEvent() {
		return new Event.Builder()
		.setStart(dateFormatter.parseDateTime(sta))
		.setDuration(new Duration(dur*60*1000))
		.setRoute(rou)
		.setParticipants(par)
		.build();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	protected final static DateTimeFormatter dateFormatter;

	static {
		dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
	}
}
