package de.greencity.bladenightapp.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

public class ListPersistor<T extends ListItem> {

	ListPersistor(Class<T> clazz) {
		this.clazz = clazz;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}


	public void setList(List<T> list) {
		this.list = Collections.synchronizedList(list);
	}

	public void write() throws IOException {
		checkDirectory();

		synchronized(list) {
			List<File> superfluousFiles = new ArrayList<File>();
			superfluousFiles.addAll(Arrays.asList(directory.listFiles()));

			Map<String, T> hits = new HashMap<String, T>();
			for (T item: list) {
				String id = item.getPersistenceId();
				if ( hits.get(id) != null )
					throw new IllegalStateException("Conflicting id " + id + "between\n" + hits.get(id).toString() + "\nand\n" + item.toString());
				hits.put(id, item);
				write(id, item);
				File file = fileOf(item);
				FileUtils.write(file, getGson().toJson(item));
				superfluousFiles.remove(file);
			}

			for (File file : superfluousFiles) {
				getLog().info("Deleting deprecated item: " + file.getAbsolutePath());
				file.delete();
			}
		}
	}

	public void write(ListItem item) throws IOException {
		write(item.getPersistenceId(), item);
	}

	private void write(String id, ListItem item) throws IOException {
		write(new File(directory, id), item);
	}

	private void write(File file, ListItem item) throws IOException {
		synchronized(list) {
			FileUtils.write(file, getGson().toJson(item));
		}
	}
	
	private File fileOf(ListItem item) {
		return new File(directory, item.getPersistenceId());
	}

	public void read() throws IOException {
		List<T> readItems = new ArrayList<T>();
		checkDirectory();
		synchronized(list) {
			File[] files = directory.listFiles();
			for ( File file : files ) {
				String fileContent = FileUtils.readFileToString(file, "UTF-8");
				readItems.add(getGson().fromJson(fileContent, clazz));
			}
			list.clear();
			list.addAll(readItems);
		}
	}

	private void checkDirectory() throws IOException {
		if ( ! directory.isDirectory() )
			throw new IOException("Not a valid directory: " + directory);
	}

	private Gson getGson() {
		if ( gson == null )
			gson = new Gson();
		return gson;
	}

	private File directory;
	private List<T> list;
	private Gson gson;
	private Class<T> clazz;

	private static Log log;

	public static void setLog(Log log) {
		ListPersistor.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(ListPersistor.class));
		return log;
	}
}
