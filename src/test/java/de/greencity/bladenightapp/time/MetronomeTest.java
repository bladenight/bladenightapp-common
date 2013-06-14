package de.greencity.bladenightapp.time;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MetronomeTest {

	@Test
	public void test() throws InterruptedException {
		long period = 20;
		Metronome metronome = new Metronome(period);
		long start = System.currentTimeMillis();
		for (int i = 1 ; i <= 5 ; i++) {
			metronome.waitNext();
			long now = System.currentTimeMillis();
			assertTrue(now > start + i * period);
			assertTrue(now < start + (i+1) * period);
		}
	}
}
