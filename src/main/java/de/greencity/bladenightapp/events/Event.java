package de.greencity.bladenightapp.events;

import java.io.Serializable;
import java.text.ParseException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.greencity.bladenightapp.persistence.ListItem;

public class Event implements ListItem, Serializable {
	private static final long serialVersionUID = -5865857910368371094L;

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

		@Override
		public String toString(){
			return "EventStatus."+status;
		}

		public String asString(){
			return status;
		}
	}

	public static class Builder {
		private Event event;

		public Builder() {
			event = new Event();
		}

		public Builder setStartDate(String dateString) throws ParseException {
			event.startDate = dateFormatter.parseDateTime(dateString);
			return this;
		}

		public Builder setStartDate(DateTime date) {
			event.setStartDate(date);
			return this;
		}

		public Builder setDuration(Duration duration) {
			event.setDuration(duration);
			return this;
		}

		public Builder setDurationInMinutes(int minutes) {
			event.setDuration(new Duration(minutes*60*1000));
			return this;
		}

		public Builder setRouteName(String routeName) {
			event.setRouteName(routeName);
			return this;
		}

		public Builder setParticipants(int participants) {
			event.setParticipants(participants);
			return this;
		}

		public Builder setStatus(EventStatus status) {
			event.setStatus(status);
			return this;
		}

		public Event build() {
			return event;
		}

	}

	Event() {
		duration = new Duration(0);
		status = EventStatus.PENDING;				
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public int getParticipants() {
		return participants;
	}

	public void setParticipants(int participants) {
		this.participants = participants;
	}

	public void setStartDate(DateTime date) {
		this.startDate = date;
	}

	public DateTime getStartDate() {
		return startDate;
	}

	public String getStartDateAsString(String format) {
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(format);
		return dateFormatter.print((DateTime)getStartDate());
	}

	public DateTime getEndDate() {
		return startDate.plus(duration);
	}

	public String getEndDateAsString(String format) {
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(format);
		return dateFormatter.print((DateTime)getEndDate());
	}


	public EventStatus getStatus() {
		return status;
	}

	public void setStatus(EventStatus status) {
		this.status = status;
	}

	@Override
	public String getPersistenceId() {
		return getStartDateAsString("yyyy-MM-dd");
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, toStringStyle);
	}

	private static Log log;

	public static void setLog(Log log) {
		Event.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(Event.class));
		return log;
	}

	protected DateTime startDate;
	protected Duration duration;
	protected String routeName;
	protected int participants;
	protected EventStatus status;

	protected final static DateTimeFormatter dateFormatter;
	protected final static ToStringStyle toStringStyle;

	static {
		dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
		toStringStyle = new ToStringStyle() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
				if (value instanceof DateTime) {
					value = dateFormatter.print((DateTime)value);
				}
				buffer.append(value);
			}
		};
	}


}
