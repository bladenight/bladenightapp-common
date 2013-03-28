package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.greencity.bladenightapp.events.Event;

@JsonAutoDetect(fieldVisibility=Visibility.ANY,getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class EventMessage {

	public enum EventStatus {
		PEN,
		CON,
		CAN
	}

	public String 		sta; 	// start time: "yyyy-MM-dd'T'HH:mm"
	public long 		dur; 	// duration in minutes
	public String 		rou; 	// route name
	public int 			par;	// number of participants
	public EventStatus 	sts;	// status
	public long			len;   	// length in meters

	public EventMessage() {
		sts = EventStatus.PEN;
	}

	public EventMessage(Event e) {
		copyFromEvent(e);
	}


	public String getStartDate() {
		return sta;
	}

	public void setStartDate(String sta) {
		this.sta = sta;
	}

	public long getDuration() {
		return dur;
	}

	public void setDuration(long dur) {
		this.dur = dur;
	}

	public String getRouteName() {
		return rou;
	}

	public void setRouteName(String rou) {
		this.rou = rou;
	}

	public int getParticipantsCount() {
		return par;
	}

	public void setParticipantsCount(int par) {
		this.par = par;
	}

	public EventStatus getStatus() {
		return sts;
	}

	public void setStatus(EventStatus sts) {
		this.sts = sts;
	}

	public long getLength() {
		return len;
	}

	public void setLength(long length) {
		this.len = length;
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
			return EventStatus.CAN;
		case CONFIRMED:
			return EventStatus.CON;
		case PENDING:
			return EventStatus.PEN;
		default:
			getLog().error("Unknown status: "+fromStatus);
			return null;
		}
	}

	Event.EventStatus convertStatus(EventStatus fromStatus) {
		switch(fromStatus) {
		case CAN:
			return Event.EventStatus.CANCELLED;
		case CON:
			return Event.EventStatus.CONFIRMED;
		case PEN:
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
