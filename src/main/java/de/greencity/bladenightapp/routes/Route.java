package de.greencity.bladenightapp.routes;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.LogFactory;
import org.geotoolkit.display.shape.ShapeUtilities;
import org.geotoolkit.geometry.DirectPosition2D;

import de.greencity.bladenightapp.geo.CoordinatesConversion;

// TODO This class has to be cleaned up / refactored, for instance:
// - copy the node list before instead of returning it the original
public final class Route {

	public static class LatLong {

		public double lat, lon;

		public LatLong(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}

		LatLong(LatLong p) {
			lat = p.lat;
			lon = p.lon;
		}

		LatLong(DirectPosition2D dp) {
			lat = dp.x;
			lon = dp.y;
		}

		DirectPosition2D toDp() {
			return new DirectPosition2D(lat,lon);
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj, true);
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	public class ProjectedLocation {
		public double linearPosition;
		public double distanceToSegment;
		public int segment;
		public double evaluation;
		
		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj, true);
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	public final double CORRIDOR_WIDTH  = 100.0;
	public final double START_PERIMETER = 100.0;

	public Route() {
		init();
	}

	private void init() {
		nodesLatLong = new ArrayList<LatLong>();
		nodesInMetricSystem = new ArrayList<DirectPosition2D>();
	}
	
	public boolean load(File file) {
		RouteKmlLoader loader = new RouteKmlLoader();
		if ( ! loader.load(file))
			return false;
		nodesLatLong = loader.getNodes();
		try {
			updateNodesInMetricSystem();
		}
		catch (Exception e) {
			nodesLatLong = null;
			return false;
		}
		updateRouteLength();
		setFilePath(file.getAbsolutePath());
		setName(FilenameUtils.removeExtension(file.getName()));
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePath() {
		return filePath;
	}

	protected void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	// The client of this class shall never modify the nodes
	public List<LatLong> getNodesLatLong() {
		return nodesLatLong;
	}


	public double getLength() {
		return length;
	}

	public int getNumberOfSegments() {
		return nodesLatLong.size()-1;
	}

	public double updateRouteLength() {
		double tmpLength = 0;
		for ( int i = 1; i<nodesLatLong.size(); i++) {
			tmpLength += CoordinatesConversion.getOrthodromicDistance(nodesLatLong.get(i-1).lat, nodesLatLong.get(i-1).lon, nodesLatLong.get(i).lat, nodesLatLong.get(i).lon);
		}
		length = tmpLength;
		return length;
	}


	private void updateNodesInMetricSystem() throws Exception {
		nodesInMetricSystem = new ArrayList<DirectPosition2D>();
		for ( LatLong latLong : nodesLatLong) {
			try {
				nodesInMetricSystem.add(CoordinatesConversion.fromLatLong(latLong.lat, latLong.lon));
			} catch (Exception e) {
				String msg = "Failed to convert coordinates in convertNodesInMetricSystem";
				getLog().error(msg, e);
				throw new Exception(msg);
			}
		}
	}

	public List<ProjectedLocation> projectPosition(double lat, double lon) {
		DirectPosition2D metricCoordinates = null;
		try {
			metricCoordinates = CoordinatesConversion.fromLatLong(lat, lon);
		} catch (Exception e) {
			getLog().error("Failed to convert coordinates in projectPosition:", e);
			return new ArrayList<ProjectedLocation>();
		}
		return projectPosition(metricCoordinates);
	}

	public List<ProjectedLocation> projectPosition(DirectPosition2D pt) {
		List<ProjectedLocation> listOfLocations = new ArrayList<ProjectedLocation>();
		double totalLength = 0.0;
		getLog().debug("Projecting point:" + pt + " on route " + name);
		for ( int i = 1; i<nodesInMetricSystem.size(); i++) {
			DirectPosition2D dp1 = nodesInMetricSystem.get(i-1);
			DirectPosition2D dp2 = nodesInMetricSystem.get(i);
			Line2D line2d = new Line2D.Double();
			line2d.setLine(dp1, dp2);
			Point2D projectedPt = ShapeUtilities.nearestColinearPoint(line2d, pt);
			double distance = pt.distance(projectedPt);

			if ( distance < CORRIDOR_WIDTH ) {
				ProjectedLocation positionOnRoute = new ProjectedLocation();
				double distanceOnSegment = dp1.distance(projectedPt);
				positionOnRoute.linearPosition = totalLength + distanceOnSegment;
				positionOnRoute.distanceToSegment = distance;
				positionOnRoute.segment = i - 1;
				getLog().debug("Found possible location on route:");
				getLog().debug("  segment=" + positionOnRoute.segment);
				getLog().debug("  linearPosition=" + positionOnRoute.linearPosition);
				getLog().debug("  distanceOnSegment=" + distanceOnSegment);
				getLog().debug("  distanceToSegment=" + positionOnRoute.distanceToSegment);
				listOfLocations.add(positionOnRoute);
			}
			totalLength += dp1.distance(dp2);
		}
		int candidates =listOfLocations.size();
		if ( candidates == 0 ) {
			getLog().debug("Sorry, couldn't project position on the route !");
		}
		else {
			getLog().debug("Found " + candidates + " candidates") ;
		}
		return listOfLocations;
	}


	// TODO create test
	public LatLong convertLinearPositionToLatLong(double linearPosition) {
		double currentSegmentSum = 0.0;
		List<LatLong> nodes = getNodesLatLong();
		synchronized (nodes) {
			for ( int nodeIndex = 0 ; nodeIndex < nodes.size()-1; nodeIndex++) {
				LatLong node1 = nodes.get(nodeIndex);
				LatLong node2 = nodes.get(nodeIndex+1);
				
				double segmentLength = CoordinatesConversion.getOrthodromicDistance(node1.lat, node1.lon, node2.lat, node2.lon);
				// node1.distance(node2);
				double missingLength = linearPosition - currentSegmentSum;
				if ( missingLength <= segmentLength  ) {
					double positionOnSegment = missingLength / segmentLength;
					// TODO this is mathematically not correct, but good enough on short distances for now 
					double lat = node1.lat + positionOnSegment * (node2.lat - node1.lat );
					double lon = node1.lon + positionOnSegment * (node2.lon - node1.lon );
					return new LatLong(lat,lon);
				}
				currentSegmentSum += segmentLength;
			}
			// Looks like the requested position is after the end of the route.
			return new LatLong(nodes.get(nodes.size()-1));
		}
	}

	protected String name;
	protected String filePath;

	protected double length;
	protected List<LatLong> nodesLatLong;
	// Unfortunately, geotooltkit doesn't seem to be able to project a point on a lat/long based curve segment
	// So we keep a copy of the nodes in a metric system 
	protected List<DirectPosition2D> nodesInMetricSystem;

	private static Log log;

	public static void setLog(Log log) {
		Route.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(Route.class));
		return log;
	}

}
