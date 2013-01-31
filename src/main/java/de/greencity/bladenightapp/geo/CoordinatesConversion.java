package de.greencity.bladenightapp.geo;

import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

public class CoordinatesConversion {

	static MathTransform transformFromLatLong;
	static MathTransform transformToLatLong;
	static GeodeticCalculator calculator;

	// "EPSG:4326"  = WGS84
	// "EPSG:32632" = UTM zone 32N
	// "EPSG:31468" = DHDN / 3-degree Gauss-Kruger zone 4
	static final String code1 = "EPSG:4326"; 
	static final String code2 = "EPSG:32632";

	static final double referenceX = 689355.0;
	static final double referenceY = 5334128.0;

	public static void initialize() throws NoSuchAuthorityCodeException, FactoryException {
		if ( transformFromLatLong == null ) {
			CoordinateReferenceSystem sourceCRS = CRS.decode(code1);
			CoordinateReferenceSystem targetCRS = CRS.decode(code2); 
			transformFromLatLong = CRS.findMathTransform(sourceCRS, targetCRS);
		}
		if ( transformToLatLong == null ) {
			CoordinateReferenceSystem sourceCRS = CRS.decode(code2); 
			CoordinateReferenceSystem targetCRS = CRS.decode(code1);
			transformToLatLong = CRS.findMathTransform(sourceCRS, targetCRS);
		}
	}
	public static DirectPosition2D fromLatLong(double latitude, double longitude) throws MismatchedDimensionException, TransformException, NoSuchAuthorityCodeException, FactoryException  {
		initialize();
		DirectPosition p = new GeneralDirectPosition(latitude, longitude);
		DirectPosition p2 = transformFromLatLong.transform(p, null);
		double x = p2.getOrdinate(0);
		double y = p2.getOrdinate(1);
		x -=  referenceX;
		y -= referenceY;
		return new DirectPosition2D(x,y);
	}

	public static DirectPosition2D toLatLong(double x, double y) throws MismatchedDimensionException, TransformException, NoSuchAuthorityCodeException, FactoryException  {
		initialize();
		DirectPosition2D p = new DirectPosition2D(x+referenceX,y+referenceY);
		DirectPosition p2 =  transformToLatLong.transform(p, null);
		return new DirectPosition2D(p2.getOrdinate(0), p2.getOrdinate(1));
	}

	public static double getOrthodromicDistance(double lat1, double long1, double lat2, double long2) {
		GeodeticCalculator calculator = new GeodeticCalculator();
		calculator.setStartingGeographicPoint(long1, lat1);
		calculator.setDestinationGeographicPoint(long2, lat2);
		return calculator.getOrthodromicDistance();

	}

	public static DirectPosition2D getPointOnLatLongSegment(double relativeLocation, double lat1, double long1, double lat2, double long2) throws MismatchedDimensionException, NoSuchAuthorityCodeException, TransformException, FactoryException {
		DirectPosition2D dp1 = fromLatLong(lat1, long1);
		DirectPosition2D dp2 = fromLatLong(lat2, long2);
		return toLatLong(dp1.x + relativeLocation*(dp2.x-dp1.x), dp1.y + relativeLocation*(dp2.y-dp1.y));
	}
}
