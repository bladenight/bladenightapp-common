package de.greencity.bladenightapp.routes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.Test;

import de.greencity.bladenightapp.events.EventList;
import de.greencity.bladenightapp.routes.Route.LatLong;
import de.greencity.bladenightapp.routes.Route.PointOnSegment;
import de.greencity.bladenightapp.routes.Route.ProjectedLocation;

public class RouteTest {

    final String path = "/de.greencity.bladenightapp.routes/Ost - lang.kml";
    private Route route;
    final private double referenceRouteLength = 16727.0;
    final private double referenceLengthPrecision = 1.0;

    @Before
    public void init() {
        Route.setLog(new NoOpLog());
        File file = FileUtils.toFile(EventList.class.getResource(path));
        route = new Route();
        assertTrue(route.load(file));
    }

    @Test
    public void checkLoad() {
        assertEquals(referenceRouteLength, route.getLength(), referenceLengthPrecision);
        assertEquals(81, route.getNumberOfSegments());
        String filePath = route.getFilePath();
        filePath = filePath.replace("\\", "/"); // For Windows
        assertTrue(filePath.endsWith(path));
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

    @Test
    public void getPartialRouteFullRoute() {
        List<Route.LatLong> list = route.getPartialRoute(0,route.getLength());
        assertEquals(route.getNumberOfSegments(), list.size());
    }

    @Test
    public void getPartialRouteStartAfterEnd() {
        List<Route.LatLong> list = route.getPartialRoute(2000,1000);
        assertEquals(0, list.size());
    }

    @Test
    public void getPartialRouteStartEqualsEnd() {
        double pos = 1000;
        List<Route.LatLong> list = route.getPartialRoute(pos, pos);
        assertEquals(2, list.size());
        Route.LatLong latLong = route.convertLinearPositionToLatLong(pos);
        assertEquals(latLong, list.get(0));
        assertEquals(latLong, list.get(1));
    }

    @Test
    public void getPartialRouteNormalCase() {
        double precision = 0.0000001;
        List<Route.LatLong> list = route.getPartialRoute(2000, 3000);
        assertEquals(4, list.size());

        assertEquals(48.138134828986175, list.get(0).lat, precision);
        assertEquals(11.52106488988085, list.get(0).lon, precision);

        assertEquals(48.14003010104577, list.get(1).lat, precision);
        assertEquals(11.51835491785466, list.get(1).lon, precision);

        assertEquals(48.14134836096864, list.get(2).lat, precision);
        assertEquals(11.51876014201025, list.get(2).lon, precision);

        assertEquals(48.14057416765811, list.get(3).lat, precision);
        assertEquals(11.526176919530192, list.get(3).lon, precision);
    }


    @Test
    public void cropPosition() {
        assertEquals(0, route.cropPosition(-1), referenceLengthPrecision);
        assertEquals(0, route.cropPosition(0), referenceLengthPrecision);
        assertEquals(1, route.cropPosition(1), referenceLengthPrecision);
        assertEquals(referenceRouteLength, route.cropPosition(referenceRouteLength), referenceLengthPrecision);
        assertEquals(referenceRouteLength, route.cropPosition(referenceRouteLength+1), referenceLengthPrecision);
    }

    @Test
    public void getPointOnSegmentForPosition() {
        PointOnSegment p = route.getPointOnSegmentForPosition(0);
        assertEquals(0, p.segmentIndex);
        assertEquals(0, p.relativePositionOnSegment,0);
        assertEquals(route.getNodesLatLong().get(0), p.latLong);

        p = route.getPointOnSegmentForPosition(291);
        assertEquals(1, p.segmentIndex);
        assertEquals(0.2115, p.relativePositionOnSegment,0.0001);
        assertEquals(109.0, p.positionOnSegment,1.0);
        assertEquals(48.13129, p.latLong.lat, 0.00001);
        assertEquals(11.53800, p.latLong.lon, 0.00001);

        // Position after the route end. We expect the last point of the route as result
        p = route.getPointOnSegmentForPosition(referenceRouteLength + 100.0);
        assertEquals(route.getNumberOfSegments()-1, p.segmentIndex);
        assertEquals(1.0, p.relativePositionOnSegment,0.0);
        assertEquals(80.0, p.positionOnSegment,1.0);
        assertEquals(route.getNodesLatLong().get(route.getNodesLatLong().size()-1), p.latLong);
    }

    @Test
    public void getPartialRouteSameSegment() {
        List<LatLong> list = route.getPartialRoute(0.0, 10.0);
        assertEquals(2, list.size());
        assertEquals(route.getNodesLatLong().get(0), list.get(0));
        assertEquals(48.1307493, route.getNodesLatLong().get(1).lat, 0.00001);
        assertEquals(11.5392264, route.getNodesLatLong().get(1).lon, 0.00001);

    }

    @Test
    public void getPartialRouteDifferentSegments() {
        List<LatLong> list = route.getPartialRoute(1000.0, 2000.0);
        assertEquals(5, list.size());
        assertEquals(48.1323665, route.getNodesLatLong().get(0).lat, 0.00001);
        assertEquals(11.5395768, route.getNodesLatLong().get(0).lon, 0.00001);

    }
    @Test
    public void getPartialRouteLastSegment() {
        List<LatLong> list = route.getPartialRoute(referenceRouteLength-10, referenceRouteLength+10);
        assertEquals(2, list.size());
        assertEquals(48.1323665, route.getNodesLatLong().get(0).lat, 0.00001);
        assertEquals(11.5395768, route.getNodesLatLong().get(0).lon, 0.00001);
        assertEquals(route.getNodesLatLong().get(route.getNodesLatLong().size()-1), list.get(1));
    }
}
