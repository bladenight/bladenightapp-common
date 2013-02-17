package de.greencity.bladenightapp.procession;

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

public class ProcessionNordKurzTest {

	final String path = "/de.greencity.bladenightapp.routes/Nord - kurz.kml";
	private Route route;
	private Procession procession;

	@BeforeClass
	static public void initClass() {
		Procession.setLog(new NoOpLog());
//		SimpleLog log = new SimpleLog("ProcessionNordKurzTest");
//		log.setLevel(SimpleLog.LOG_LEVEL_ALL);
//		Procession.setLog(log);
//		SegmentedProcession.setLog(log);
//		ParticipantUpdater.setLog(log);
	}

	@Before
	public void init() {
		File file = FileUtils.toFile(EventsList.class.getResource(path));
		route = new Route();
		assertTrue(route.load(file));
		procession = new Procession();
		procession.setRoute(route);
	}

	@Test
	public void singleParticipantEvolvingOnOverlappingSegments() {
		String participantId = generateParticipantId();
		
		// Start on an overlap to make it hard
		double lat1 = 48.141338;
		double lon1 = 11.548417;
		updateParticipant(participantId, lat1, lon1);
		assertProcessionIn(1450, 1500);

		// Move further on the overlapping segment:
		double lat2 = 48.145580;
		double lon2 = 11.558034;
		updateParticipant(participantId, lat2, lon2);
		assertProcessionIn(2740, 2750);

		// Further, but not on the overlap anymore:
		double lat3 = 48.157715;
		double lon3 = 11.574209;
		updateParticipant(participantId, lat3, lon3);
		assertProcessionIn(5710, 5720);

		// Back the same point as before, but now on the return path
		updateParticipant(participantId, lat2, lon2);
		assertProcessionIn(9850, 9860);
		
	}

	private void updateParticipant(String participantId, double lat, double lon) {
		ParticipantInput input = new ParticipantInput(participantId, lat, lon); 
		procession.updateParticipant(input);
		procession.computeProcession();
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
