package de.greencity.bladenightapp.procession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greencity.bladenightapp.events.EventsList;
import de.greencity.bladenightapp.routes.Route;

public class ProcessionTest {

	final String path = "/de.greencity.bladenightapp.routes/Ost - lang.kml";
	private Route route;
	private Procession procession;

	@BeforeClass
	static public void initClass() {
		Procession.setLog(new NoOpLog());
		TravelTimeComputer.setLog(new NoOpLog());
		HeadAndTailComputer.setLog(new NoOpLog());
	}

	@Before
	public void init() {
		File file = FileUtils.toFile(EventsList.class.getResource(path));
		route = new Route();
		assertTrue(route.load(file));
		assertTrue(route.getLength() > 0);
		procession = new Procession();
		procession.setRoute(route);
	}

	@Test
	public void testInitialConfiguration() {
		assertEquals(0, procession.getParticipantCount());
	}

	@Test
	public void singleParticipantOnRoute() {
		String deviceId = addParticipant(48.134750, 11.531566);
		assertEquals(1, procession.getParticipantCount());
		assertProcessionIn(850, 1000);
		assertTrue(procession.getLength() < 100);
		assertTrue(procession.getHead().isInProcession());
		assertTrue(procession.getHead().isOnRoute());
		assertTrue(procession.getTail().isInProcession());
		assertTrue(procession.getTail().isOnRoute());
		assertTrue(procession.getParticipant(deviceId).getLastKnownPoint().isInProcession());
		assertTrue(procession.getParticipant(deviceId).getLastKnownPoint().isOnRoute());
	}

	@Test
	public void singleParticipantOffRoute() {
		String deviceId = addParticipant(0,0);
		assertEquals(1, procession.getParticipantCount());
		assertTrue(! procession.getHead().isInProcession());
		assertTrue(! procession.getHead().isOnRoute());
		assertTrue(! procession.getTail().isInProcession());
		assertTrue(! procession.getTail().isOnRoute());
		assertTrue(! procession.getParticipant(deviceId).getLastKnownPoint().isInProcession());
		assertTrue(! procession.getParticipant(deviceId).getLastKnownPoint().isOnRoute());
	}

	@Test
	public void autoCompute() {
		ParticipantInput input = new ParticipantInput("autoComputeDisabled", true, 48.134750, 11.531566); 
		procession.updateParticipant(input);
		assertEquals(1, procession.getParticipantCount());
		assertEquals(0, procession.getHeadPosition(), 0.0);
		procession.setMaxComputeAge(0);
		// Head position shall be updated with explicit call to compute()
		assertEquals(909, procession.getHeadPosition(), 1.0);
	}

	@Test
	public void singleParticipantEvolvingOnOverlappingSegments() {
		String participantId = generateParticipantId();

		// assertEquals(0.0, procession.evaluateTravelTimeBetween(0.0, 10000.0), 0.0);

		// Start on an overlap to make it hard
		double lat1 = 48.128642;
		double lon1 = 11.555716;
		updateParticipant(participantId, lat1, lon1);
		assertProcessionIn(6080, 6095);

		// assertEquals(0.0, procession.evaluateTravelTimeBetween(0.0, 10000.0), 0.0);

		// Move further on the overlapping segment:
		double lat2 = 48.124311;
		double lon2 = 11.563947;
		updateParticipant(participantId, lat2, lon2);
		assertProcessionIn(6880, 6900);

		// assertTrue(procession.evaluateTravelTimeBetween(0.0, 10000.0) > 0.0);

		// Further, but not on the overlap anymore:
		double lat3 = 48.100605;
		double lon3 = 11.552637;
		updateParticipant(participantId, lat3, lon3);
		assertProcessionIn(10935, 10945);

		// Back the same point as before, but now on the return path
		updateParticipant(participantId, lat2, lon2);
		assertProcessionIn(14300, 14310);

		// Set participant back to beginning of the route to make sure
		// the algorithm is able to reset itself
		updateParticipant(participantId, 48.139941, 11.536054);
		assertProcessionIn(3735, 3740);
	}


	@Test
	public void multipleParticipants() {
		double lat1 = 48.135607;
		double lon1 = 11.524631;

		double lat2 = 48.139625;
		double lon2 = 11.518710;

		int nParticipants = 100;
		for ( int i = 0 ; i < nParticipants ; i++) {
			double lat = lat1 + (lat2-lat1) * i / nParticipants;
			double lon = lon1 + (lon2-lon1) * i / nParticipants;
			addParticipant(lat, lon);
		}
		assertEquals(nParticipants, procession.getParticipantCount());

		assertProcessionIn(1600, 2300);

		assertTrue(procession.getLength() > 500);
	}
	
	@Test
	public void removeParticipant() {
		String id1 = addParticipant(48.135607, 11.524631);
		String id2 = addParticipant(48.139625, 11.518710);
		assertEquals(2, procession.getParticipantCount());
		procession.removeParticipant(id1);
		assertEquals(1, procession.getParticipantCount());
		assertTrue(procession.getParticipant(id2) != null);
		assertTrue(procession.getParticipant(id1) == null);
	}


	private String addParticipant(double lat, double lon) {
		String participantId = generateParticipantId();
		updateParticipant(participantId, lat, lon);
		return participantId;
	}

	private void updateParticipant(String participantId, double lat, double lon) {
		ParticipantInput input = new ParticipantInput(participantId, true, lat, lon); 
		procession.updateParticipant(input);
		procession.compute();
	}

	private String generateParticipantId() {
		return UUID.randomUUID().toString();
	}

	private void assertProcessionIn(double min, double max) {
		MovingPoint head = procession.getHead();
		MovingPoint tail = procession.getTail();

		assertTrue(head.getLinearPosition() >= min);
		assertTrue(head.getLinearPosition() <= max);

		assertTrue(tail.getLinearPosition() >= min);
		assertTrue(tail.getLinearPosition() <= max);

		assertTrue(head.getLinearPosition() >= tail.getLinearPosition());
	}
}
