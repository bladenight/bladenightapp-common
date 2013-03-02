package de.greencity.bladenightapp.procession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.greencity.bladenightapp.routes.Route.ProjectedLocation;
import de.greencity.bladenightapp.time.Sleep;

public class ParticipantUpdaterTest {

	@Test
	public void test() throws InterruptedException {
		double tailPosition = 1000.0;
		double headPosition = 2000.0;
		double routeLength  = 5000.0;
		double lat = 10, lon = 20;
		ParticipantInput participantInput = new ParticipantInput("client", lat, lon);
		Participant participant = new Participant();

		double expectedLocation1 = 1500;
		ParticipantUpdater updater = new ParticipantUpdater.Builder().
				setProcessionEnds(tailPosition, headPosition).
				setParticipantInput(participantInput).
				setParticipant(participant).
				setPotentialLocations(createPotentialLocations(expectedLocation1, 3000)).
				setRouteLength(routeLength).
				build();
		updater.updateParticipant();
		assertEquals(expectedLocation1, participant.getLinearPosition(), 0.0);
		assertEquals(lat, participant.getLastKnownPoint().getLatitude(), 0.0);
		assertEquals(lon, participant.getLastKnownPoint().getLongitude(), 0.0);
		assertEquals(0.0, participant.getLastKnownPoint().getLinearSpeed(), 0.0);
		
		double expectedLocation2 = expectedLocation1 + 100;
		updater.setPotentialLocations(createPotentialLocations(expectedLocation2, 3000));
		Sleep.sleep(1); // the speed can not be computed unless some time elapsed
		updater.updateParticipant();
		assertEquals(expectedLocation2, participant.getLinearPosition(), 0.0);
		assertTrue(participant.getLinearSpeed() > 0.0);
	}
	
	List<ProjectedLocation> createPotentialLocations(double pos1, double pos2) {
		List<ProjectedLocation> potentialLocations = new ArrayList<ProjectedLocation>();
		potentialLocations.add(new ProjectedLocation(0.0, pos1));
		potentialLocations.add(new ProjectedLocation(0.0, pos2));
		return potentialLocations;
	}
}
