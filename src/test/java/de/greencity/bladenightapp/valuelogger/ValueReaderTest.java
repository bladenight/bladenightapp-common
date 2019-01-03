package de.greencity.bladenightapp.valuelogger;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.greencity.bladenightapp.events.EventList;
import de.greencity.bladenightapp.valuelogger.ValueReader.Entry;

public class ValueReaderTest {

    class MyConsumer implements ValueReader.Consumer {

        @Override
        public void consume(Entry entry) {
            counter++;
            if ( counter == 1 ) {
                assertEquals("2012-06-25T21:42:48.039+02:00", entry.getString("ts"));
                assertEquals("anonymized-1", entry.getString("did"));
                assertEquals(48.22, entry.getDouble("la"), 0.0001);
                assertEquals(11.33, entry.getDouble("lo"), 0.0001);
                assertEquals(10, entry.getDouble("ac"), 0.0001);
            }
            else if ( counter == 2 ) {
                assertEquals("2012-06-25T21:42:48.039+02:00", entry.getString("ts"));
                assertEquals("anonymized-2", entry.getString("did"));
                assertEquals(49.22, entry.getDouble("la"), 0.0001);
                assertEquals(12.33, entry.getDouble("lo"), 0.0001);
                assertEquals(10, entry.getDouble("ac"), 0.0001);
            }
        }
        long counter = 0;
    }

    @Test
    public void test() throws IOException {

        final String path = "/de.greencity.bladenightapp.valuelogger/valuereadertest.txt";
        File file = FileUtils.toFile(EventList.class.getResource(path));

        MyConsumer consumer = new MyConsumer();
        ValueReader valueReader = new ValueReader(file, consumer);
        valueReader.read();

        assertEquals(2, consumer.counter);
    }
}
