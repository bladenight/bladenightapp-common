package de.greencity.bladenightapp.protocol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Protocol {

	public Protocol(File file) throws IOException {
		writer = new BufferedWriter(new FileWriter(file, true));
	}


	public void write(String tag1, String tag2, String msg) {
		try {
			String line = System.currentTimeMillis() + "\t" + tag1 + "\t" + tag2 +"\t" + msg + "\n";
			writer.write(line);
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
}
