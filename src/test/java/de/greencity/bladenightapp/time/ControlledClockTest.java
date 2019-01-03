package de.greencity.bladenightapp.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ControlledClockTest {
    @Test
    public void defaultTimeFromSystem() throws InterruptedException {
        ControlledClock clock = new ControlledClock();
        long time = clock.currentTimeMillis();
        assertTrue(time <= System.currentTimeMillis() );
        assertTrue(System.currentTimeMillis() - time < 1000);
    }

    @Test
    public void initialTime() throws InterruptedException {
        long time = 12345678;
        ControlledClock clock = new ControlledClock(time);
        assertEquals(time, clock.currentTimeMillis());
    }

    @Test
    public void setTime() throws InterruptedException {
        long time = 12345678;
        ControlledClock clock = new ControlledClock();
        clock.set(time);
        assertEquals(time, clock.currentTimeMillis());
    }

    @Test
    public void incrementTime() throws InterruptedException {
        long time = 12345678;
        long increment = 1234;
        ControlledClock clock = new ControlledClock();
        clock.set(time);
        clock.increment(increment);
        assertEquals(time + increment, clock.currentTimeMillis());
    }

}
