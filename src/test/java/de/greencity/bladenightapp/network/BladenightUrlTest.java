package de.greencity.bladenightapp.network;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BladenightUrlTest {

	// This test is more to improve test coverage than to fix bugs
	@Test
	public void test() {
		String asText = BladenightUrl.GET_ACTIVE_EVENT.getText();
		assertEquals(asText, "http://www.greencity.de/bladenight/app/rpc/getActiveEvent");
		
		String asString = BladenightUrl.GET_ACTIVE_EVENT.toString();
		assertEquals(asString, "BladenightUrl:"+asText);
	}
}
