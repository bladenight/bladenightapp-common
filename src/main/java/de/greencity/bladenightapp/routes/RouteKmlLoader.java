package de.greencity.bladenightapp.routes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.greencity.bladenightapp.routes.Route.LatLong;

public class RouteKmlLoader {
	public RouteKmlLoader() {
		
	}

	public RouteKmlLoader(File file) {
		load(file);
	}

	public boolean load(File file) {
		boolean result;
		try {
			result = loadFromStreamAndClose(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			String path = file.getAbsolutePath();
			getLog().error("Could not read \"" + path + "\"", e);
			return false;
		}
		return result;
	}
	
	public List<LatLong> getNodes() {
		return nodesLatLong;
	}

	private boolean loadFromStreamAndClose(InputStream kmlInputStream) {
		boolean result = loadFromOpenStream(kmlInputStream);
		try {
			kmlInputStream.close();
		} catch (IOException e) {
			// Not much to do...
		}
		return result;
	}


	private boolean loadFromOpenStream(InputStream kmlInputStream) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			getLog().error("Could not create a new document builder", e);
			return false;
		}
		Document doc;
		try {
			doc = documentBuilder.parse(kmlInputStream);
		} catch (SAXException e) {
			getLog().error("Could not read XML from stream (malformed?)", e);
			return false;
		} catch (IOException e) {
			getLog().error("Could not read XML from stream (access problem?)", e);
			return false;
		}
		doc.getDocumentElement().normalize();

		nodesLatLong = new ArrayList<Route.LatLong>();;

		NodeList nList = doc.getElementsByTagName("Placemark");
		String coordinatesString = getTagValue("coordinates", (Element) nList.item(0));
		String[] coordinatesList = coordinatesString.split("[\\s]+");
		for (String coordinateString : coordinatesList) {
			if ( coordinateString.matches("[\\s]*"))
				continue;
			if ( ! parseAndAddNodeOrLog(coordinateString) )
				return false;
		}
		return true;
	}

	private boolean parseAndAddNodeOrLog(String coordinateString) {
		String[] coordinateFields = coordinateString.split(",");

		double longitude;
		double latitude;
		try {
			longitude = Double.parseDouble(coordinateFields[0]);
			latitude = Double.parseDouble(coordinateFields[1]);
		}
		catch(NumberFormatException e) {
			getLog().error("Unable to parse coordinates: " + coordinateString, e);
			return false;
		}

		nodesLatLong.add(new Route.LatLong(latitude, longitude));

		return true;
	}
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}

	private static Log log;

	public static void setLog(Log log) {
		RouteKmlLoader.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(RouteKmlLoader.class));
		return log;
	}
	
	private List<Route.LatLong> nodesLatLong;

}
