package de.greencity.bladenightapp.network.messages;

import static org.junit.Assert.*;

import org.junit.Test;

import de.greencity.bladenightapp.network.messages.MovingPointMessage;
import de.greencity.bladenightapp.procession.MovingPoint;

public class MovingPointTest {

	@Test
	public void newFromMovingPoint() {
		MovingPoint mp = new MovingPoint();
		mp.setLatitude(10.0);
		mp.setLongitude(20.0);
		mp.setLinearPosition(50.0);
		mp.setLinearSpeed(100.0);
		mp.isInProcession(true);
		mp.isOnRoute(true);
		MovingPointMessage netMp = new MovingPointMessage(mp);
		assertEquals(mp.getLatitude(), netMp.getLatitude(), 0.0);
		assertEquals(mp.getLongitude(), netMp.getLongitude(), 0.0);
		assertEquals(mp.getLinearPosition(), netMp.getPosition(), 0.0);
		assertEquals(mp.getLinearSpeed(), netMp.getSpeed(), 0.0);
		assertEquals(mp.isOnRoute(), netMp.isOnRoute());
		assertEquals(mp.isInProcession(), netMp.isInProcession());
	}

	@Test
	public void copyFromMovingPoint() {
		MovingPoint mp = new MovingPoint();
		mp.setLatitude(10.0);
		mp.setLongitude(20.0);
		mp.setLinearPosition(50.0);
		mp.setLinearSpeed(100.0);
		mp.isInProcession(true);
		mp.isOnRoute(true);
		MovingPointMessage netMp = new MovingPointMessage();
		netMp.copyFrom(mp);
		assertEquals(mp.getLatitude(), netMp.getLatitude(), 0.0);
		assertEquals(mp.getLongitude(), netMp.getLongitude(), 0.0);
		assertEquals(mp.getLinearPosition(), netMp.getPosition(), 0.0);
		assertEquals(mp.getLinearSpeed(), netMp.getSpeed(), 0.0);
		assertEquals(mp.isOnRoute(), netMp.isOnRoute());
		assertEquals(mp.isInProcession(), netMp.isInProcession());
	}
}
