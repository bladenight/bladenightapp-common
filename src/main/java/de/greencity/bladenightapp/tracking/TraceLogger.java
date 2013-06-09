package de.greencity.bladenightapp.tracking;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class TraceLogger {

	public enum FIELD {
		TIMESTAMP("ts"),
		LATITUDE("la"),
	    LONGITUDE("lo"),
	    ACCURACY("ac"),
	    LINEAR_POSITION("lp"),
	    ;
	    /**
	     * @param text
	     */
	    private FIELD(final String text) {
	        this.text = text;
	    }

	    private final String text;

	    @Override
	    public String toString() {
	        return text;
	    }
	}
	
	public TraceLogger(File traceFile) {
		this.traceFile = traceFile;
	}
	
	public void setTimestamp(DateTime dateTime) {
		this.timestamp = dateTime;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public void setLinearPosition(double linearPosition) {
		this.linearPosition = linearPosition;
	}

	public void write() throws IOException {
		String line = buildLine();
		FileUtils.writeStringToFile(traceFile, line, true);
		lastWriteTime = new DateTime();
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

		builder.append(FIELD.TIMESTAMP);
		builder.append(SEPARATOR_VALUE);
		builder.append(dateTimeFormatter.print(getUserDefinedTimestampOrNow()));

		clearUserDefinedTimestamp();

		builder.append(SEPARATOR_FIELD);

		builder.append(FIELD.LATITUDE);
		builder.append(SEPARATOR_VALUE);
		builder.append(latitude);

		builder.append(SEPARATOR_FIELD);

		builder.append(FIELD.LONGITUDE);
		builder.append(SEPARATOR_VALUE);
		builder.append(longitude);

		builder.append(SEPARATOR_FIELD);

		builder.append(FIELD.LINEAR_POSITION);
		builder.append(SEPARATOR_VALUE);
		builder.append(linearPosition);

		builder.append(SEPARATOR_FIELD);

		builder.append(FIELD.ACCURACY);
		builder.append(SEPARATOR_VALUE);
		builder.append(accuracy);

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
	private double latitude;
	private double longitude;
	private double linearPosition;
	private double accuracy;
	private DateTime timestamp;
	private DateTime lastWriteTime;
	
	static final String SEPARATOR_VALUE = "="; 
	static final String SEPARATOR_FIELD = "\t";
	static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
}
