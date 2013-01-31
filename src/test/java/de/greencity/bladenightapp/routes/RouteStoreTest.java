package de.greencity.bladenightapp.routes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greencity.bladenightapp.events.EventsList;


public class RouteStoreTest {
	RouteStore routeStore;

	@BeforeClass
	static public void initializeClass() {
		RouteStore.setLog(new NoOpLog());
		RouteKmlLoader.setLog(new NoOpLog());
	}

	@Before
	public void initialize() {
		File baseDir = FileUtils.toFile(EventsList.class.getResource("/de.greencity.bladenightapp.routes/"));
		routeStore = new RouteStore(baseDir);
	}

	@Test
	public void getAvailableRoutes() {
		List<String> list = routeStore.getAvailableRoutes();
		assertTrue(list.contains("Nord - kurz"));
		assertTrue(list.contains("Ost - kurz"));
		assertTrue(list.contains("Ost - lang"));
		assertTrue(list.contains("West - kurz"));
		assertTrue(list.contains("West - lang"));
	}

	@Test
	public void getExistingRoute() {
		String routeName = "Nord - kurz";
		Route route = routeStore.getRoute(routeName);
		assertNotNull(route);
		// Make sure the right route has been loaded by the store:
		assertEquals(12606, route.getLength(), 1.0);
		// the route name must set by the store:
		assertEquals(routeName, route.getName());
	}

	@Test
	public void getExistingRoute2() {
		String routeName = "Ost - lang";
		Route route = routeStore.getRoute(routeName);
		assertNotNull(route);
		// Make sure the right route has been loaded by the store:
		assertEquals(16728, route.getLength(), 1.0);
		// the route name must set by the store:
		assertEquals(routeName, route.getName());
	}

	@Test
	public void loadInvalidFile() {
		String routeName = "invalid-kml-file";
		Route route = routeStore.getRoute(routeName);
		assertNull(route);
	}

	@Test
	public void loadDirectory() {
		String routeName = "directory";
		Route route = routeStore.getRoute(routeName);
		assertNull(route);
	}

	@Test
	public void getNonExistingRoute() {
		Route route = routeStore.getRoute("Non existing route");
		assertNull(route);
	}
}
