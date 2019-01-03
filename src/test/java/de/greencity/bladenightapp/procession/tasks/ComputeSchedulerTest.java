package de.greencity.bladenightapp.procession.tasks;

import static org.junit.Assert.*;

import org.junit.Test;

import de.greencity.bladenightapp.time.Sleep;

public class ComputeSchedulerTest {

    public static class Computable implements ComputeSchedulerClient {
        int computed;
        @Override
        public void compute() {
            computed++;
        }

    }

    @Test
    public void test() throws InterruptedException {
        int period = 50;
        Computable procession = new Computable();
        ComputeScheduler scheduler = new ComputeScheduler(procession, period);
        Thread thread = new Thread(scheduler);
        assertEquals(0, procession.computed);
        thread.start();
        for (int i=0; i<10; i++)
            if ( procession.computed == 0)
                Sleep.sleep(period);
        assertTrue(procession.computed > 0);
    }
}
