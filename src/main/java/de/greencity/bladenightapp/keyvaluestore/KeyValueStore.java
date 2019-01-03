package de.greencity.bladenightapp.keyvaluestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class for stores of key/value string pairs.
 */
public abstract class KeyValueStore implements KeyValueStoreInterface  {

    abstract public boolean readExternalResource(String identifier);

    abstract public boolean writeExternalResource(String identifier);

    abstract public String getString(String key);

    abstract public void setString(String key, String value);

    public String getString(String key, String defaultValue) {
        String value = getString(key);
        if ( value == null )
            return defaultValue;
        else
            return value;
    }

    public String getNonNullString(String key) throws IllegalArgumentException {
        String value = getString(key);
        if ( value == null ) {
            String msg = "Property \"" + key + "\" is not defined in \"" + identifier + "\"";
            getLog().error(msg);
            throw(new IllegalArgumentException(msg));
        }
        return value;
    }

    public long getLong(String key, long defaultValue) {
        String value = getString(key);
        if ( value == null )
            return defaultValue;
        else
            return Long.parseLong(value);
    }

    public int getInt(String key, int defaultValue) {
        String value = getString(key);
        if ( value == null )
            return defaultValue;
        else
            return Integer.parseInt(value);
    }

    public double getDouble(String key, double defaultValue) {
        String value = getString(key);
        if ( value == null )
            return defaultValue;
        else
            return Double.parseDouble(value);
    }

    public Date getDate(String key, String defaultValue) throws ParseException {
        String value = getString(key);
        if ( value == null )
            return parseDateString(defaultValue);
        else
            return parseDateString(value);
    }

    public Date getDate(String key) throws IllegalArgumentException, ParseException {
        String value = getNonNullString(key);
        return parseDateString(value);
    }

    private Date parseDateString(String dateString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("y-M-d H:m");
        return format.parse(dateString);
    }

    public String getPath(String key) {
        return getPath(key, null);
    }

    public String getPath(String key, String defaultPath) {
        String value = getString(key);
        if ( value == null ) {
            if ( defaultPath != null ) {
                value = defaultPath;
            }
            else {
                logUndefinedKey("w", key);
                return null;
            }
        }
        // TODO refactor and make OS independent
        if ( ! value.startsWith("/") && basePath != null ) {
            // Relative path
            return basePath + "/" + value;
        }
        else {
            return value;
        }
    }

    void logUndefinedKey(String level, String key) {
        String msg = "Property \"" + key + "\" is not defined in \"" + identifier + "\"";
        if ( level.equals("i")) {
            getLog().info(msg);
        }
        else if ( level.equals("t")) {
            getLog().trace(msg);
        }
        else if ( level.equals("w")) {
            getLog().warn(msg);
        }
        else if ( level.equals("t")) {
            getLog().error(msg);
        }
    }
    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * Used to retrieve and save the store, and also in the log
     */
    protected String identifier;

    /**
     * Base path for the path values which are relative.
     */
    protected String basePath;

    private static Log log;

    public static void setLog(Log log) {
        KeyValueStore.log = log;
    }

    protected static Log getLog() {
        if (log == null)
            log = LogFactory.getLog(KeyValueStore.class);
        return log;
    }
}
