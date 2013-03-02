package de.greencity.bladenightapp.procession;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.apache.commons.logging.impl.SimpleLog;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greencity.bladenightapp.time.ControlledClock;

public class TravelTimeComputerTest {
	TravelTimeComputer computer;

	@BeforeClass
	public static void beforeClass() {
//		SimpleLog log = new SimpleLog("SpeedMapComputerTest");
//		log.setLevel(0);
//		SpeedMapComputer.setLog(log);
	}

	@Before
	public void before() {
		computer = new TravelTimeComputer(100);
		computer.setRouteLength(10000.0);
	}

	@Test(expected=IllegalStateException.class)
	public void noRouteLength() {
		computer = new TravelTimeComputer(100);
		computer.evaluateTravelTimeBetween(0.0, 2000.0);
	}

	@Test
	public void singleStationaryParticipant() {
		computer.updateParticipant(generateParticipantId(), 1000.0, 0.0);
		assertEquals(0.0, computer.evaluateTravelTimeBetween(0.0, 2000.0), 0.00);
	}

	@Test
	public void singleMovingParticipantSingleUpdate() {
		double speed = 10.0; // km/h
		computer.updateParticipant(generateParticipantId(), 1000.0, speed);
		assertEquals(0.0, computer.evaluateTravelTimeBetween(0.0, 2000.0), 0.00);
	}

	@Test
	public void singleMovingParticipantMultipleUpdates() {
		double initialPosition = 5000.0;
		double newPosition = 6000.0;
		long deltaTime = 60000;
		double speedKmh = 3600.0 * (newPosition - initialPosition) / deltaTime;
		String deviceId = generateParticipantId();
		ControlledClock clock = new ControlledClock();
		computer.setClock(clock);
		computer.updateParticipant(deviceId, initialPosition, speedKmh );
		clock.increment(deltaTime);
		computer.updateParticipant(deviceId, newPosition, speedKmh);

		double oneThird = (newPosition - initialPosition) / 3.0;
		double expectedTime =  deltaTime / 3.0;
		assertEquals(expectedTime, computer.evaluateTravelTimeBetween(initialPosition + oneThird, initialPosition+ 2.0*oneThird), expectedTime/100.0);
	}

	@Test
	public void multipleMovingParticipantMultipleUpdates() {
		double initialPosition = 0.0;
		long deltaTime = 1000;
		double speedKmh = 60.0;
		double processionLength = 1000.0;
		ControlledClock clock = new ControlledClock(0);
		computer.setClock(clock);

		int nIterations = 60;
		int nParticipants = 50;
		double maxPos = 0;
		for (int iteration=0; iteration<nIterations; iteration++) {
			long time = clock.currentTimeMillis();
			double deltaPos = speedKmh * time / 3600;
			for (int participant=0; participant<nParticipants; participant++) {
				double offset = processionLength * participant / nParticipants;
				double newPosition = initialPosition + offset + deltaPos;
				computer.updateParticipant("RUNNING-" + participant, newPosition, speedKmh );
				maxPos = Math.max(maxPos,newPosition);
			}
			clock.increment(deltaTime);
		}
		{
			double expectedTime =  3600 * (maxPos - initialPosition) / (speedKmh);
			assertEquals(expectedTime, computer.evaluateTravelTimeBetween(initialPosition, maxPos), expectedTime / 100);
		}
		{
			assertEquals(0.0, computer.evaluateTravelTimeBetween(maxPos + 2 * computer.getSegmentLength(), computer.getRouteLength()), 0.0);
		}
	}

	private String generateParticipantId() {
		return UUID.randomUUID().toString();
	}
}