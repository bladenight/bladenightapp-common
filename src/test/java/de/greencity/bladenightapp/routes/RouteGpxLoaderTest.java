package de.greencity.bladenightapp.routes;

import de.greencity.bladenightapp.events.EventList;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RouteGpxLoaderTest {
    private RouteGpxLoader routeOsmLoader;
    @BeforeClass
    static public void initializeClass() {
    }

    @Before
    public void initialize() {
        routeOsmLoader = new RouteGpxLoader();
    }

    @Test
    public void load() {
        String path = "/de.greencity.bladenightapp.routes/Nord - kurz.gpx";
        File file = FileUtils.toFile(EventList.class.getResource(path));
        assertTrue(routeOsmLoader.load(file));
        List<Route.LatLong> nodes = routeOsmLoader.getNodes();

        assertEquals(74, nodes.size());
        assertEquals(new Route.LatLong(48.132397852, 11.54380815), nodes.get(0));
    }
}
