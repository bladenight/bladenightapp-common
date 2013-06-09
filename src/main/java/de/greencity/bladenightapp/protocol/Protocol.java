package de.greencity.bladenightapp.protocol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Protocol {

	public static final String WAMPIN = "WAMPIN";
	
	public Protocol(File file) throws IOException {
		writer = new BufferedWriter(new FileWriter(file, true));
	}


	public synchronized void write(String type, String ...colums) {
		try {
			String timeString = dateTimeFormatter.print(new DateTime());
			String[] timeArray = {timeString, type};
			String[] both = ArrayUtils.addAll(timeArray, colums);
			String line = StringUtils.join(both,"\t");
			writer.write(line + "\n");
			writer.flush();
		}
		catch(IOException e) {
			getLog().error(e);
		}
	}


	public void close() {
		try {
			writer.close();
		}
		catch(IOException e) {
			getLog().error(e);
		}

	}

	private static Log log;

	public static void setLog(Log log) {
		Protocol.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(Protocol.class));
		return log;
	}

	// private File file;
	private BufferedWriter writer;
	static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
}
