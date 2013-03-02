package de.greencity.bladenightapp.time;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class SystemClockTest {

	@Test
	public void test() throws InterruptedException {
		SystemClock clock = new SystemClock(); 
		long time1 = clock.currentTimeMillis();
		Sleep.sleep(1);
		long time2 = clock.currentTimeMillis();
		assertTrue(time2 <= System.currentTimeMillis() );
		assertTrue(System.currentTimeMillis() - time2 < 1000);
		assertTrue("time currentTimeMillis shall increase", time2 > time1);
	}
}
