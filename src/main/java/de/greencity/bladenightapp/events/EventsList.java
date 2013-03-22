package de.greencity.bladenightapp.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

public class EventsList implements Iterable<Event> {

	public EventsList() {
		events = new ArrayList<Event>();
	}

	public static EventsList newFromDir(File dir) {
		EventsList list = new EventsList();
		for (File file : getEventFilesIn(dir)) {
			Event event = null;
			event = Event.newFromFile(file);
			if ( event != null )
				list.addEvent(event);
			else
				getLog().error("Could not load event from file: " + file.getAbsolutePath());
		}
		return list;
	}

	public void writeToDir(File folder) throws IOException {
		List<File> superfluousFiles = getEventFilesIn(folder);
		for ( Event event : events) {
			// That will work fine, until we have more than one events per day:
			String fileName = event.getStartDateAsString("yyyy-MM-dd") + "." + EVENT_FILE_EXTENSION;
			getLog().info("Writing: " + fileName);
			File file = new File(folder, fileName);
			event.writeToFile(file);
			superfluousFiles.remove(file);
		}
		for (File file : superfluousFiles) {
			getLog().info("Deleting: " + file.getAbsolutePath());
			file.delete();
		}
	}

	public Event getNextEvent() {
		Event nextEvent = null;
		DateTime now = new DateTime();
		for ( Event event : events ) {
			if ( now.isBefore(event.getEndDate()) ) {
				if ( nextEvent == null || event.getStartDate().isBefore(nextEvent.getStartDate()) )
					nextEvent = event;
			}

		}
		return nextEvent;
	}

	public static List<File> getEventFilesIn(File dir) {
		List<File> list = new ArrayList<File>();
		for ( File child: new ArrayList<File>(Arrays.asList(dir.listFiles()))) {
			if ( isEventFile(child) ) {
				 getLog().debug("Found event file: " + child.getAbsolutePath());
				list.add(child);
			}
			else {
				 getLog().debug("Discarding: " + child.getAbsolutePath());
			}
		}
		return list;
	}

	public static boolean isEventFile(File file) {
		if ( ! file.isFile() )
			return false;
		String extension = FilenameUtils.getExtension(file.getName());
		if ( ! EVENT_FILE_EXTENSION.equals(extension) )
			return false;
		return true;
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public int size() {
		return events.size();
	}
	
	public int indexOf(Event event) {
		return events.indexOf(event);
	}

	@Override
	public Iterator<Event> iterator() {
		return events.iterator();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static Log log;

	public static void setLog(Log log) {
		EventsList.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(EventsList.class));
		return log;
	}

	protected List<Event> events;
	final static public String EVENT_FILE_EXTENSION = "evt";

}
