package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.greencity.bladenightapp.events.Event;

public class EventMessage {

	public enum EventStatus {
		PENDING("P"),
		CONFIRMED("O"),
		CANCELLED("A");

		private final String status;       

		private EventStatus(String s) {
			status = s;
		}

		public boolean equalsName(String otherName){
			return (otherName == null)? false:status.equals(otherName);
		}

		public String toString(){
			return status;
		}
	}

	public String 		sta; 	// start time: "yyyy-MM-dd'T'HH:mm"
	public long 		dur; 	// duration in minutes
	public String 		rou; 	// route name
	public int 			par;	// number of participants
	public EventStatus 	sts;

	public EventMessage() {
		sts = EventStatus.PENDING;
	}

	public EventMessage(Event e) {
		copyFromEvent(e);
	}

	public void copyFromEvent(Event e) {
		sta = dateFormatter.print(e.getStartDate());
		dur = e.getDuration().getStandardMinutes();
		rou = e.getRouteName();
		par = e.getParticipants();
		sts = convertStatus(e.getStatus());
	}

	public static EventMessage newFromEvent(Event e) {
		EventMessage message = new EventMessage();
		message.copyFromEvent(e);
		return message;
	}
	
	public Event toEvent() {
		return new Event.Builder()
		.setStartDate(dateFormatter.parseDateTime(sta))
		.setDuration(new Duration(dur*60*1000))
		.setRouteName(rou)
		.setParticipants(par)
		.setStatus(convertStatus(sts))
		.build();
	}

	EventStatus convertStatus(Event.EventStatus fromStatus) {
		switch(fromStatus) {
		case CANCELLED:
			return EventStatus.CANCELLED;
		case CONFIRMED:
			return EventStatus.CONFIRMED;
		case PENDING:
			return EventStatus.PENDING;
		default:
			getLog().error("Unknown status: "+fromStatus);
			return null;
		}
	}

	Event.EventStatus convertStatus(EventStatus fromStatus) {
		switch(fromStatus) {
		case CANCELLED:
			return Event.EventStatus.CANCELLED;
		case CONFIRMED:
			return Event.EventStatus.CONFIRMED;
		case PENDING:
			return Event.EventStatus.PENDING;
		default:
			getLog().error("Unknown status: "+fromStatus);
			return null;
		}
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

	private static Log log;

	public static void setLog(Log log) {
		EventMessage.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(EventMessage.class));
		return log;
	}
}
