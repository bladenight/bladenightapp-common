package de.greencity.bladenightapp.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import de.greencity.bladenightapp.events.Event.EventStatus;

public class EventList implements Iterable<Event> {

	public EventList() {
		events = new ArrayList<Event>();
	}

	public static EventList newFromDir(File dir) {
		EventList list = new EventList();
		list.setPersistenceFolder(dir);
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
		setPersistenceFolder(folder);
	}

	public void writeToDir() throws IOException {
		if ( persistenceFolder == null )
			throw new IllegalStateException("No persistence folder provided so far");
		writeToDir(persistenceFolder);
	}

	public File getPersistenceFolder() {
		return persistenceFolder;
	}

	public void setPersistenceFolder(File persistenceFolder) {
		this.persistenceFolder = persistenceFolder;
	}

	public Event getActiveEvent() {
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
	
	public void setActiveRoute(String routeName) {
		Event event = getActiveEvent();
		if ( event == null ) {
			getLog().error("setActiveRoute: No current event found");
			return;
		}
		event.setRouteName(routeName);
	}

	public void setActiveStatus(EventStatus newStatus) {
		Event event = getActiveEvent();
		if ( event == null ) {
			getLog().error("setActiveStatus: No current event found");
			return;
		}
		event.setStatus(newStatus);
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

	public Event get(int pos) {
		return events.get(pos);
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
		EventList.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(EventList.class));
		return log;
	}

	protected List<Event> events;
	protected File persistenceFolder;

	final static public String EVENT_FILE_EXTENSION = "evt";

}
