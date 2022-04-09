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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ListPersistor<T extends ListItem> {

    public ListPersistor(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ListPersistor(Class<T> clazz, File directory) {
        this.clazz = clazz;
        setDirectory(directory);
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
                File file = fileFor(item);
                FileUtils.write(file, getGson().toJson(item));
                superfluousFiles.remove(file);
            }

            for (File file : superfluousFiles) {
                if ( isPersistenceFile(file)) {
                    getLog().info("Deleting deprecated item: " + file.getAbsolutePath());
                    file.delete();
                }
            }
        }
    }

    public void write(ListItem item) throws IOException {
        write(item.getPersistenceId(), item);
    }

    private void write(String id, ListItem item) throws IOException {
        write(fileFor(item), item);
    }

    private void write(File file, ListItem item) throws IOException {
        synchronized(list) {
            if ( list.contains(item))
                FileUtils.write(file, getGson().toJson(item));
            else if ( file.exists() )
                file.delete();
        }
    }

    private File fileFor(ListItem item) {
        return new File(directory, item.getPersistenceId() + "." + EXTENSION);
    }

    private boolean isPersistenceFile(File file) {
        return EXTENSION.equals(FilenameUtils.getExtension(file.getName()));
    }


    public void read() throws IOException, InconsistencyException {
        List<T> readItems = new ArrayList<T>();
        checkDirectory();

        synchronized(list) {
            File[] files = directory.listFiles();
            Arrays.sort(files);
            for ( File file : files ) {
                if ( isPersistenceFile(file) ) {
                    T item = appendFile(file, readItems);
                    String id = item.getPersistenceId();
                    String baseName = FilenameUtils.getBaseName(file.getName());
                    if ( ! baseName.equals(id) )
                        throw new InconsistencyException("Discrepency found. Expecting:=" + id + " Got:" + baseName);
                }
            }
            list.clear();
            list.addAll(readItems);
        }
    }

    private T appendFile(File file, List<T> readItems) throws IOException {
        String fileContent = FileUtils.readFileToString(file, "UTF-8");
        T item = null;
        try {
            item = getGson().fromJson(fileContent, clazz);
        }
        catch(Exception e) {
            getLog().error(e.toString());
        }
        if ( item == null ) {
            throw new IOException("Could not parse " + file);
        }
        readItems.add(item);
        return item;
    }

    private void checkDirectory() throws IOException {
        if ( ! directory.isDirectory() )
            throw new IOException("Not a valid directory: " + directory);
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    private Gson getGson() {
        if ( gson == null ) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }
        return gson;
    }

    private File directory;
    private List<T> list;
    private Gson gson;
    private Class<T> clazz;
    static final String EXTENSION = "per";

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
