package de.greencity.bladenightapp.events;

import java.text.ParseException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.greencity.bladenightapp.events.Event.Builder;

public class Event {

	public static class Builder {
		private Event event;

		public Builder() {
			event = new Event();
		}

		public Builder setStart(String dateString) throws ParseException {
			event.startDate = dateFormatter.parseDateTime(dateString);
			return this;
		}

		public Builder setStart(DateTime date) {
			event.startDate = date;
			return this;
		}

		public Builder setRoute(String routeName) {
			event.routeName = routeName;
			return this;
		}

		public Builder setDuration(Duration duration) {
			event.duration = duration;
			return this;
		}

		public Builder setMinutes(int minutes) {
			event.duration = new Duration(minutes*60*1000);
			return this;
		}

		public Builder setRouteName(String routeName) {
			event.routeName = routeName;
			return this;
		}

		public Builder setParticipants(int participants) {
			event.participants = participants;
			return this;
		}

		public Event build() {
			return event;
		}
	}
	
	Event() {
		duration = new Duration(0);
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

	public DateTime getStartDate() {
		return startDate;
	}

	public DateTime getEndDate() {
		return startDate.plus(duration);
	}


	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, toStringStyle);
	}

	protected DateTime startDate;
	protected Duration duration;
	protected String routeName;
	protected int participants;
	
	protected final static DateTimeFormatter dateFormatter;
	protected final static ToStringStyle toStringStyle;

	static {
		dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
		toStringStyle = new ToStringStyle() {
			private static final long serialVersionUID = 1L;

			protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
				if (value instanceof DateTime) {
					value = dateFormatter.print((DateTime)value);
				}
				buffer.append(value);
			}
		};
	}

}
