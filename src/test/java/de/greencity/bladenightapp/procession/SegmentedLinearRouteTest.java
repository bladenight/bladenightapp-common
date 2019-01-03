package de.greencity.bladenightapp.procession;

import static org.junit.Assert.*;

import org.junit.Test;

public class SegmentedLinearRouteTest {
    @Test(expected=IllegalArgumentException.class)
    public void wrongInit() {
        new SegmentedLinearRoute(0);

    }
    @Test(expected=IllegalStateException.class)
    public void noRouteLength() {
        SegmentedLinearRoute route = new SegmentedLinearRoute(10);
        route.getSegmentForLinearPosition(1000);
    }

    @Test
    public void getSegmentForLinearPosition() {
        int nSegments = 100;
        SegmentedLinearRoute route = new SegmentedLinearRoute(100);
        double routeLength = 5000.0;
        route.setRouteLength(routeLength);
        assertEquals(nSegments, route.getNumberOfSegments());
        assertEquals(routeLength, route.getRouteLength(), 0.0);
        assertEquals(nSegments/2, route.getSegmentForLinearPosition(routeLength/2));
        assertEquals(nSegments-1, route.getSegmentForLinearPosition(routeLength*2));
        assertEquals(0, route.getSegmentForLinearPosition(-routeLength));
    }

    @Test
    public void getPositionOfSegment() {
        int nSegments = 100;
        SegmentedLinearRoute route = new SegmentedLinearRoute(100);
        double routeLength = 5000.0;
        route.setRouteLength(routeLength);
        assertEquals( 2 * routeLength / nSegments, route.getPositionOfSegmentStart(2), 0.0);
        assertEquals( 3 * routeLength / nSegments, route.getPositionOfSegmentEnd(2), 0.0);
    }
}
