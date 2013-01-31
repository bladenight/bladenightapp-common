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

public class GsonHelper {
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
	
	public static Gson getGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter());
		builder.registerTypeAdapter(Duration.class, new DurationTypeConverter());
		return builder.create();
	}

	static public String toJson(Event event) {
		return getGson().toJson(event);
	}
}
