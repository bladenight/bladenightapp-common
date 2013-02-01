package de.greencity.bladenightapp.procession;

import static org.junit.Assert.*;

import org.junit.Test;

import de.greencity.bladenightapp.utils.Sleep;

public class MovingPointTest {

	@Test
	public void initialValues() {
		MovingPoint movingPoint = new MovingPoint();
		assertFalse(movingPoint.isInProcession());
		assertFalse(movingPoint.isOnRoute());
		assertEquals(0, movingPoint.getLatitude(), 0);
		assertEquals(0, movingPoint.getLongitude(), 0);
		assertEquals(0, movingPoint.getLinearPosition(), 0);
		assertEquals(0, movingPoint.getLinearSpeed(), 0);
		assertTrue(System.currentTimeMillis() - movingPoint.getTimestamp() < 1000);
		assertTrue(movingPoint.getAge() < 1000);
	}

	@Test
	public void testSettersAndGetters() {
		MovingPoint movingPoint = new MovingPoint();
		movingPoint.isInProcession(true);
		assertTrue(movingPoint.isInProcession());
		movingPoint.isOnRoute(true);
		assertTrue(movingPoint.isOnRoute());
	}

	@Test
	public void testSpeed() throws InterruptedException {
		double initialPosition = 100;
		double finalPosition = 101;
		long sleep = 50;
		double theoriticalSpeed = (finalPosition - initialPosition) * 3600 / sleep;
		MovingPoint movingPoint = new MovingPoint();
		movingPoint.update(10.1, 10.2, initialPosition);
		Sleep.sleep(sleep);
		movingPoint.update(20.1, 20.2, finalPosition);
		assertEquals(finalPosition, movingPoint.getLinearPosition(), 0);
		double precision = sleep / 5; // The longer the sleep period, the better the precision must get
		assertEquals(theoriticalSpeed, movingPoint.getLinearSpeed(), movingPoint.getLinearPosition() / precision );
	}
}
