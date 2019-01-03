package de.greencity.bladenightapp.keyvaluestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

public class KeyValueStorePropertyBased extends KeyValueStore {
    static protected String propertiesFilePath;
    static protected String propertiesFileParentPath;

    static protected Properties properties;

    @Override
    public boolean readExternalResource(String path) {
        properties = new Properties() {
            private static final long serialVersionUID = -1493034935918113014L;

            // http://stackoverflow.com/questions/54295/how-to-write-java-util-properties-to-xml-with-sorted-keys
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
        propertiesFilePath = path;
        String absolutePath = new File(propertiesFilePath).getAbsolutePath();
        propertiesFileParentPath = new File(absolutePath).getParent();
        setBasePath(propertiesFileParentPath);
        FileInputStream in = null;
        try {
            in = new FileInputStream(propertiesFilePath);
            properties.load(in);
        } catch (FileNotFoundException e) {
            getLog().error("Unable to read properties from file + \"" + path + "\"", e);
            return false;
        } catch (IOException e) {
            getLog().error("Unable to read properties from file + \"" + path + "\"", e);
            return false;
        }
        finally {
            try {
                if ( in != null )
                    in.close();
            }
            catch(Exception e) {}
        }
        return true;
    }

    @Override
    public boolean writeExternalResource(String identifier) {
        String tmpFile = propertiesFilePath + ".tmp";
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tmpFile);
            properties.store(out, "writeExternalResource");
        } catch (FileNotFoundException e) {
            getLog().error("Unable to write properties to file + \"" + tmpFile + "\"", e);
            return false;
        } catch (IOException e) {
            getLog().error("Unable to write properties to file + \"" + tmpFile + "\"", e);
            return false;
        }
        finally {
            if ( out != null )
                try {
                    out.close();
                } catch (IOException e) {
                }
        }
        new File(tmpFile).renameTo(new File(propertiesFilePath));
        return true;
    }

    @Override
    public String getString(String key) {
        return properties.getProperty(key);
    }

    @Override
    public void setString(String key, String value) {
        properties.setProperty(key,value);
    }

}
