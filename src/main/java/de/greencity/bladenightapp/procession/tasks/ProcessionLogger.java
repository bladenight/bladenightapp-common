package de.greencity.bladenightapp.procession.tasks;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.procession.Procession;
import de.greencity.bladenightapp.time.Metronome;
import de.greencity.bladenightapp.valuelogger.ValueLogger;


public class ProcessionLogger implements Runnable {
	
	public ProcessionLogger(File traceFile, Procession procession, long period) {
		this.procession = procession;
		this.period = period;
		this.valueLogger = new ValueLogger(traceFile);
	}
	
	@Override
	public void run() {
		boolean cont = true;
		Metronome metronome = new Metronome(period);
		while (cont) {
			try {
				write();
				metronome.waitNext();
			} catch (InterruptedException e) {
				cont = false;
			} catch (IOException e) {
				log.error("Failed to write: " + e);
			}
		}
	}
	
	public boolean shallContinue() {
		return true;
	}

	public void write() throws IOException {
		long headPosition = (long)procession.getHeadPosition();
		long tailPosition = (long)procession.getTailPosition();
		if (headPosition <= 0 )
			return;
		valueLogger.flushAllValues();
		valueLogger.setValue(FIELD.HEAD_POS.toString(), Long.toString(headPosition));
		valueLogger.setValue(FIELD.TAIL_POS.toString(), Long.toString(tailPosition));
		valueLogger.write();
	}
	

	public enum FIELD {
		HEAD_POS("hp"),
	    TAIL_POS("tp"),
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
	
	private Procession procession;
	private long period;
	private ValueLogger valueLogger;
	
	private static Log log;

	public static void setLog(Log log) {
		ProcessionLogger.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(ProcessionLogger.class));
		return log;
	}
}
