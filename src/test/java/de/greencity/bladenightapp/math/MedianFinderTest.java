package de.greencity.bladenightapp.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MedianFinderTest {
	@Test
	public void simple1() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(2.0, 1.0);
		medianFinder.addWeightedValue(3.0, 1.0);
		assertEquals(2.5, medianFinder.findMedian(),0.0);
	}

	@Test
	public void simple2() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(2.0,   1.0);
		medianFinder.addWeightedValue(3.0,   1.0);
		medianFinder.addWeightedValue(10.0, 10.0);

		assertEquals((3.0*1.0 + 10.0*10.0) / 11.0, medianFinder.findMedian(), 0.0);
	}

	@Test
	public void simple3() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(2.0,  10.0);
		medianFinder.addWeightedValue(3.0,   1.0);
		medianFinder.addWeightedValue(10.0,  1.0);

		assertEquals((2.0*10.0 + 3.0*1.0) / 11.0, medianFinder.findMedian(), 0.0);
	}

	@Test
	public void quantil25() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(-100.0, 0.1);
		for ( int i = 0 ; i <= 100 ; i++)
			medianFinder.addWeightedValue(i / 10.0, 1.0);
		medianFinder.addWeightedValue(100.0, 0.1);
		assertEquals(2.5, medianFinder.findMedian(0.25),0.0);
	}

	@Test
	public void quantil75() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(-100.0, 0.1);
		for ( int i = 0 ; i <= 100 ; i++)
			medianFinder.addWeightedValue(i * 10.0, 1.0);
		medianFinder.addWeightedValue(10000.0, 0.1);
		assertEquals(750.0, medianFinder.findMedian(0.75),0.0);
	}


	@Test
	public void getTotal() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(2.0,  10.0);
		medianFinder.addWeightedValue(3.0,   1.0);
		medianFinder.addWeightedValue(10.0,  1.0);

		assertEquals(12.0, medianFinder.getTotalWeight(), 0.0);
	}

	@Test
	public void redundantValues() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(1.0,   2.0);
		medianFinder.addWeightedValue(1.0,   2.0);
		medianFinder.addWeightedValue(1.0,   2.0);
		medianFinder.addWeightedValue(10.0,  1.0);

		assertEquals( 1.0, medianFinder.findMedian(), 0.0);
	}

	@Test
	public void complex() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(0.5,     0.5);
		medianFinder.addWeightedValue(1.0,     2.0);
		medianFinder.addWeightedValue(2.0,     1.0);
		medianFinder.addWeightedValue(5.0,     2.0);
		medianFinder.addWeightedValue(100.0,   0.5);

		assertEquals(2.8, medianFinder.findMedian(), 0.0);
	}

	@Test
	public void negative() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(-10.0,   1.0);
		medianFinder.addWeightedValue(-20.0,   1.0);
		assertEquals(-15.0, medianFinder.findMedian(),0.0);
	}


	@Test
	public void unsorted() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(9.0,   1.0);
		medianFinder.addWeightedValue(-11.0, 1.0);
		medianFinder.addWeightedValue(110.0, 0.1);
		medianFinder.addWeightedValue(-110.0, 0.1);
		medianFinder.addWeightedValue(-1.0,  1.0);
		assertEquals(-1.0, medianFinder.findMedian(),0.0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void zeroWeight() {
		MedianFinder medianFinder = new MedianFinder();
		medianFinder.addWeightedValue(1.0, 0.0);
	}

	@Test
	public void sampleCount() {
		MedianFinder medianFinder = new MedianFinder();
		assertEquals(0, medianFinder.sampleCount());
		medianFinder.addWeightedValue(1.0,   1.0);
		assertEquals(1, medianFinder.sampleCount());
		medianFinder.addWeightedValue(2.0,   1.0);
		assertEquals(2, medianFinder.sampleCount());
	}

	@Test(expected=IllegalStateException.class)
	public void noValue() {
		MedianFinder medianFinder = new MedianFinder();
		assertEquals(0.0, medianFinder.findMedian(), 0.0);
	}
}
