package de.greencity.bladenightapp.geo;

import static org.junit.Assert.*;

import org.geotoolkit.geometry.DirectPosition2D;
import org.junit.Before;
import org.junit.Test;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

public class CoordinatesConversionTest {
    @Before
    public void init() throws NoSuchAuthorityCodeException, FactoryException {
        CoordinatesConversion.initialize();
    }
    @Test
    public void fromLatLong() throws MismatchedDimensionException, NoSuchAuthorityCodeException, TransformException, FactoryException {
        double lat = 48.132414;
        double lon = 11.543588;
        DirectPosition2D dp2d = CoordinatesConversion.fromLatLong(lat, lon);
        assertEquals(-107.5, dp2d.x, 0.1);
        assertEquals(18.9, dp2d.y, 0.1);

        DirectPosition2D dpLatLon = CoordinatesConversion.toLatLong(dp2d.x, dp2d.y);
        assertEquals(lat, dpLatLon.x, 0.000000001);
        assertEquals(lon, dpLatLon.y, 0.000000001);
    }

    @Test
    public void getOrthodromicDistance() {
        double distance = CoordinatesConversion.getOrthodromicDistance(48.132414, 11.543588, 48.209348, 11.648217);
        assertEquals(11564.7, distance, 0.1);
    }

    @Test
    public void interpolateLatLongSegment() throws MismatchedDimensionException, NoSuchAuthorityCodeException, TransformException, FactoryException {
        DirectPosition2D dp = CoordinatesConversion.interpolateLatLongSegment(0.5, 48.132414, 11.543588, 48.209348, 11.648217);
        assertEquals(48.17089374, dp.x, 0.00000001);
        assertEquals(11.59586323, dp.y, 0.00000001);
    }
}
