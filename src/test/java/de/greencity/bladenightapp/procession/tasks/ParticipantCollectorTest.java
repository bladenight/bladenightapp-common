package de.greencity.bladenightapp.procession.tasks;


import static org.junit.Assert.*;

import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greencity.bladenightapp.procession.ParticipantInput;
import de.greencity.bladenightapp.procession.Procession;
import de.greencity.bladenightapp.time.ControlledClock;

public class ParticipantCollectorTest {
    @BeforeClass
    public static void initClass() {
        ParticipantCollector.setLog(new NoOpLog());
    }

    @Before
    public void init() {
        clock = new ControlledClock();
        procession = new Procession(clock);
        collector = new ParticipantCollector(procession);
        collector.setClock(clock);

    }

    @Test
    public void testNoLimits() {

        clock.set(0);
        procession.updateParticipant(new ParticipantInput("1", true, 0, 0));
        clock.set(12*3600*1000);
        procession.updateParticipant(new ParticipantInput("2", true, 0, 0));
        clock.set(24*3600*1000);

        assertEquals(2,procession.getParticipantCount());

        collector.collect();

        assertEquals(2,procession.getParticipantCount());
    }

    @Test
    public void testAbsoluteMaxTime() {

        clock.set(0);
        procession.updateParticipant(new ParticipantInput("1", true, 0, 0));
        assertEquals(1, procession.getParticipantCount());

        clock.set(20);

        collector.setMaxAbsoluteAge(10);
        collector.collect();

        assertEquals(0,procession.getParticipantCount());
    }

    @Test
    public void testRelativeMaxTime() {
        String deviceIdToBeRemoved = "to-be-removed";
        String deviceIdToBeKept = "to-be-kept";
        int nOtherParticipants = 100;

        long referenceTime = 10000;

        clock.set(referenceTime - 5000);
        procession.updateParticipant(new ParticipantInput(deviceIdToBeRemoved, true, 0, 0));
        procession.updateParticipant(new ParticipantInput(deviceIdToBeKept, true, 0, 0));
        for ( int i = 0 ; i < nOtherParticipants ; i ++)
            procession.updateParticipant(new ParticipantInput(Integer.toString(i), true, 0, 0));
        assertEquals(nOtherParticipants+2, procession.getParticipantCount());

        clock.set(referenceTime - 2000);
        procession.updateParticipant(new ParticipantInput(deviceIdToBeKept, true, 0, 0));

        clock.set(referenceTime - 1000);
        for ( int i = 0 ; i < nOtherParticipants ; i ++)
            procession.updateParticipant(new ParticipantInput(Integer.toString(i), true, 0, 0));

        clock.set(referenceTime);

        collector.setMaxRelativeAgeFactor(2.5);
        collector.collect();

        assertEquals(nOtherParticipants+1, procession.getParticipantCount());
        assertTrue(procession.getParticipant(deviceIdToBeRemoved) == null);
        assertTrue(procession.getParticipant(deviceIdToBeKept) != null);
    }


    ControlledClock clock;
    Procession procession;
    ParticipantCollector collector;
}
