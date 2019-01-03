package de.greencity.bladenightapp.keyvaluestore;

import java.text.ParseException;
import java.util.Date;

public interface KeyValueStoreInterface {

    abstract public boolean readExternalResource(String identifier);

    abstract public boolean writeExternalResource(String identifier);

    abstract public String getString(String key);

    abstract public void setString(String key, String value);

    public abstract String getString(String key, String defaultValue);

    public abstract String getNonNullString(String key)
            throws IllegalArgumentException;

    public abstract long getLong(String key, long defaultValue);

    public abstract int getInt(String key, int defaultValue);

    public abstract double getDouble(String key, double defaultValue);

    public abstract Date getDate(String key, String defaultValue)
            throws ParseException;

    public abstract Date getDate(String key) throws IllegalArgumentException,
            ParseException;

    public String getPath(String key);

    public abstract String getPath(String key, String defaultPath);

    public abstract String getBasePath();

    public abstract void setBasePath(String basePath);

}