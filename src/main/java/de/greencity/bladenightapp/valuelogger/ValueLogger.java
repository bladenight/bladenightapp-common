package de.greencity.bladenightapp.valuelogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class ValueLogger {

	public ValueLogger(File traceFile) {
		this.traceFile = traceFile;
		this.values = new HashMap<String, String>();
		flushAllValues();
	}


	public void flushAllValues() {
		values.clear();
	}


	public void setTimestamp(DateTime dateTime) {
		this.timestamp = dateTime;
	}

	public void setValue(String key, String value) {
		
		if (TIMESTAMP_KEY.equals(key))
			throw new IllegalArgumentException("Invalid value, reserved for internal use: " + key );
		
		synchronized(values) {
			values.put(key, value);
		}
	}

	public void write() throws IOException {
		synchronized(values) {
			String line = buildLine();
			FileUtils.writeStringToFile(traceFile, line, true);
			lastWriteTime = new DateTime();
		}
	}

	public void writeWithTimeLimit(long timeLimitInMs) throws IOException {
		if ( lastWriteTime != null ) {
			long lastWriteAge = new Duration(lastWriteTime, new DateTime()).getMillis();
			if ( lastWriteAge < timeLimitInMs )
				return;
		}
		write();
	}


	private String buildLine() {
		StringBuilder builder = new StringBuilder();

		builder.append(TIMESTAMP_KEY);
		builder.append(SEPARATOR_VALUE);
		builder.append(dateTimeFormatter.print(getUserDefinedTimestampOrNow()));
		
		Set<String> sortedKeys = new TreeSet<String>(values.keySet());
		for(String key: sortedKeys) {
			builder.append(SEPARATOR_FIELD);
			builder.append(key);
			builder.append(SEPARATOR_VALUE);
			builder.append(values.get(key));
		}

		clearUserDefinedTimestamp();

		builder.append("\n");

		return builder.toString();
	}

	private DateTime getUserDefinedTimestampOrNow() {
		if ( timestamp == null )
			return new DateTime();
		return timestamp;
	}

	private void clearUserDefinedTimestamp() {
		timestamp = null;
	}

	private File traceFile;
	private DateTime timestamp;
	private DateTime lastWriteTime;
	Map<String, String> values;

	static final String SEPARATOR_VALUE = "="; 
	static final String SEPARATOR_FIELD = "\t";
	static final String TIMESTAMP_KEY = "ts";
	static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
}
