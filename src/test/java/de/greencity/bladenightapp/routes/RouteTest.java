package de.greencity.bladenightapp.routes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.Test;

import de.greencity.bladenightapp.events.EventsList;
import de.greencity.bladenightapp.routes.Route.LatLong;
import de.greencity.bladenightapp.routes.Route.ProjectedLocation;

public class RouteTest {

	final String path = "/de.greencity.bladenightapp.routes/Ost - lang.kml";
	private Route route;
	
	@Before
	public void init() {
		Route.setLog(new NoOpLog());
		File file = FileUtils.toFile(EventsList.class.getResource(path));
		route = new Route();
		assertTrue(route.load(file));
	}

	@Test
	public void checkLoad() {
		assertEquals(16727, route.getLength(), 1.0);
		assertEquals(81, route.getNumberOfSegments());
		assertTrue(route.getFilePath().endsWith(path));
		LatLong firstNode = route.getNodesLatLong().get(0);
		assertEquals(48.13236655362276, firstNode.lat, 0.0000001);
		assertEquals(11.53957684075901, firstNode.lon, 0.0000001);
	}
	
	@Test
	public void projectPosition1() {
		double lat = 48.132002;
		double lon = 11.555804;
		List<Route.ProjectedLocation> projectedLocations = route.projectPosition(lat, lon);
		assertEquals(9, projectedLocations.size());
	}

	@Test
	public void projectPosition2() {
		double lat = 48.140652;
		double lon = 11.537731;
		List<Route.ProjectedLocation> projectedLocations = route.projectPosition(lat, lon);
		assertEquals(1, projectedLocations.size());
		ProjectedLocation l = projectedLocations.get(0);
		assertEquals(95, l.distanceToSegment, 1);
		assertEquals(3856, l.linearPosition, 1);
		assertEquals(9, l.segment);
	}


	@Test
	public void convertLinearPositionToLatLong1() {
		Route.LatLong latLong = route.convertLinearPositionToLatLong(100);
		assertEquals(48.13147653034604, latLong.lat, 0.0000001);
		assertEquals(11.539383984019604, latLong.lon, 0.0000001);
	}

	@Test
	public void convertLinearPositionToLatLong2() {
		Route.LatLong latLong = route.convertLinearPositionToLatLong(1000);
		assertEquals(48.13530166971159, latLong.lat, 0.0000001);
		assertEquals(11.53067341323588, latLong.lon, 0.0000001);
	}

	@Test
	public void convertLinearPositionToLatLong3() {
		Route.LatLong latLong = route.convertLinearPositionToLatLong(10000);
		assertEquals(48.10365084245633, latLong.lat, 0.0000001);
		assertEquals(11.561568708527084, latLong.lon, 0.0000001);
	}
}
