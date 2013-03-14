package de.greencity.bladenightapp.network.messages;

import static org.junit.Assert.*;

import org.junit.Test;

public class LatLongTest {
	@Test
	public void test() {
		double lat = 12.345;
		double lon = 6.789;
		LatLong l = new LatLong(lat, lon);
		assertEquals(lat, l.getLatitude(),1/LatLong.precision);
		assertEquals(lon, l.getLongitude(),1/LatLong.precision);
	}
}
