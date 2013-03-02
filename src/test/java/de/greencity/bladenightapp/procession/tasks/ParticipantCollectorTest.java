package de.greencity.bladenightapp.procession.tasks;

import static org.junit.Assert.*;

import org.junit.Test;

import de.greencity.bladenightapp.time.Sleep;

public class ParticipantCollectorTest {
	final static double FACTOR = 4.0;
	
	public static class Collectable implements ParticipantCollectorClient {
		int called;
		@Override
		public void removeOutdatedParticipants(double factor) {
			if (factor == FACTOR)
				called++;
		}
		
	}
	
	@Test
	public void test() throws InterruptedException {
		int period = 50;
		Collectable procession = new Collectable();
		ParticipantCollector collector = new ParticipantCollector(procession, FACTOR, period);
		Thread thread = new Thread(collector);
		assertEquals(0, procession.called);
		thread.start();
		for (int i=0; i<10; i++)
			if ( procession.called == 0)
				Sleep.sleep(period);
		assertTrue(procession.called > 0);
	}
}
