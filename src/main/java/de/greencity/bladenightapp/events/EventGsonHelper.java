package de.greencity.bladenightapp.events;

import java.lang.reflect.Type;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.greencity.bladenightapp.events.Event.EventStatus;

public class EventGsonHelper {
	static private class DurationTypeConverter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
		final static int factor = 60 * 1000;
		@Override
		public JsonElement serialize(Duration src, Type srcType, JsonSerializationContext context) {
			return new JsonPrimitive(src.getMillis() / factor);
		}

		@Override
		public Duration deserialize(JsonElement json, Type type, JsonDeserializationContext context)
				throws JsonParseException {
			return new Duration(json.getAsLong() * factor);
		}
	}

	static private class DateTimeTypeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
		@Override
		public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}

		@Override
		public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
				throws JsonParseException {
			try {
				return new DateTime(json.getAsString());
			} catch (IllegalArgumentException e) {
				// May be it came in formatted as a java.util.Date, so try that
				Date date = context.deserialize(json, Date.class);
				return new DateTime(date);
			}
		}
	}	

	static private class EventStatusConvert implements JsonSerializer<EventStatus>, JsonDeserializer<EventStatus>
	{
		@Override
		public EventStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException
				{
			EventStatus[] allStati = EventStatus.values();
			for (EventStatus status : allStati)
			{
				if (status.asString().equals(json.getAsString()))
					return status;
			}
			return null;
				}

		@Override
		public JsonElement serialize(EventStatus src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(src.asString());
		}
	}
	public static Gson getGson() {
		if ( gson == null ) {
			GsonBuilder builder = new GsonBuilder();
			builder.setPrettyPrinting();
			builder.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter());
			builder.registerTypeAdapter(Duration.class, new DurationTypeConverter());
			builder.registerTypeAdapter(EventStatus.class, new EventStatusConvert());
			gson = builder.create();
		}
		return gson;
	}

	static public String toJson(Event event) {
		return getGson().toJson(event);
	}

	static Gson gson = null;
}
