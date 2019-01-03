package de.greencity.bladenightapp.testutils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

public class Files {
    static public File createTemporaryFolder() throws IOException  {
        File file = File.createTempFile("tmpfolder", ".d");
        file.delete();
        file.mkdir();
        assertTrue(file.exists());
        assertTrue(file.isDirectory());
        return file;
    }

}
