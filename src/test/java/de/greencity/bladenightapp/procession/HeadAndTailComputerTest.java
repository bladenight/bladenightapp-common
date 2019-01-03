package de.greencity.bladenightapp.procession;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greencity.bladenightapp.procession.ProcessionParticipantsListener.ParticipantData;

public class HeadAndTailComputerTest {
    HeadAndTailComputer computer;

    @BeforeClass
    public static void beforeClass() {
//      SimpleLog log = new SimpleLog("HeadAndTailComputerTest");
//      log.setLevel(0);
//      HeadAndTailComputer.setLog(log);
    }

    @Before
    public void before() {
        computer = new HeadAndTailComputer(100);
        computer.setRouteLength(10000.0);
    }

    @Test(expected=IllegalStateException.class)
    public void noRouteLength() {
        computer = new HeadAndTailComputer(100);
        computer.compute();
    }

    @Test
    public void singleParticipant() {
        computer.updateParticipant(generateParticipantId(), new ParticipantData(1000.0, 0.0, 0.0));
        computer.compute();
        assertEquals(1000.0, computer.getTailPosition(), 1.0);
        assertEquals(1000.0, computer.getHeadPosition(), 1.0);
    }

    @Test
    public void twoParticipants() {
        computer.updateParticipant(generateParticipantId(), new ParticipantData(1000.0, 0.0, 0.0));
        computer.updateParticipant(generateParticipantId(), new ParticipantData(1100.0, 0.0, 0.0));
        computer.compute();
        assertEquals(1000.0, computer.getTailPosition(), 1.0);
        assertEquals(1100.0, computer.getHeadPosition(), 1.0);
    }

    @Test
    public void speedBonus() {
        computer.updateParticipant(generateParticipantId(), new ParticipantData(1000.0, 0.0, 0.0));
        computer.updateParticipant(generateParticipantId(), new ParticipantData(1100.0, 0.0, 0.0));
        computer.updateParticipant(generateParticipantId(), new ParticipantData(8000.0, 10.0, 0.0));
        computer.updateParticipant(generateParticipantId(), new ParticipantData(8100.0, 10.0, 0.0));
        computer.compute();
        assertEquals(8000.0, computer.getTailPosition(), 1.0);
        assertEquals(8100.0, computer.getHeadPosition(), 1.0);
    }

    @Test
    public void accuracyBonus() {
        computer.updateParticipant(generateParticipantId(), new ParticipantData(1000.0, 10.0, 1000.0));
        computer.updateParticipant(generateParticipantId(), new ParticipantData(1100.0, 10.0, 1000.0));
        computer.updateParticipant(generateParticipantId(), new ParticipantData(8000.0, 10.0, 10.0));
        computer.compute();
        assertEquals(8000.0, computer.getTailPosition(), 1.0);
        assertEquals(8000.0, computer.getHeadPosition(), 1.0);
    }

    @Test
    public void removingParticipants() {
        String id1 = "Dyn-1";
        String id2 = "Dyn-2";
        computer.updateParticipant("Static-1", new ParticipantData(1000.0, 0.0, 0.0));
        computer.updateParticipant("Static-2", new ParticipantData(1100.0, 0.0, 0.0));
        computer.updateParticipant(id1, new ParticipantData(8000.0, 10.0, 0.0));
        computer.updateParticipant(id2, new ParticipantData(8100.0, 10.0, 0.0));
        computer.compute();
        computer.removeParticipant(id1);
        computer.removeParticipant(id2);
        computer.compute();
        assertEquals(1000.0, computer.getTailPosition(), 1.0);
        assertEquals(1100.0, computer.getHeadPosition(), 1.0);
    }

    private String generateParticipantId() {
        return UUID.randomUUID().toString();
    }

//  private void assertProcessionHeadIn(double min, double max) {
//      assertDoubleIn(computer.getHeadPosition(), min, max);
//  }
//
//  private void assertProcessionTailIn(double min, double max) {
//      assertDoubleIn(computer.getTailPosition(), min, max);
//  }
//
//  private void assertProcessionIn(double min, double max) {
//      assertDoubleIn(computer.getHeadPosition(), min, max);
//      assertDoubleIn(computer.getTailPosition(), min, max);
//  }
//  private void assertDoubleIn(double value, double min, double max) {
//      assertTrue(value >= min);
//      assertTrue(value <= max);
//  }

}
