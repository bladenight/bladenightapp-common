package de.greencity.bladenightapp.routes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.greencity.bladenightapp.events.EventList;

public class RouteKmlLoaderTest {
    private RouteKmlLoader routeKmlLoader;
    @BeforeClass
    static public void initializeClass() {
    }

    @Before
    public void initialize() {
        routeKmlLoader = new RouteKmlLoader();
    }

    @Test
    public void load() {
        String path = "/de.greencity.bladenightapp.routes/Nord - kurz.kml";
        File file = FileUtils.toFile(EventList.class.getResource(path));
        assertTrue(routeKmlLoader.load(file));
        List<Route.LatLong> nodes = routeKmlLoader.getNodes();

        assertEquals(76, nodes.size());
        assertEquals(new Route.LatLong(48.13246449995051, 11.54349921573263), nodes.get(0));
    }

}
