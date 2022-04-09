package de.greencity.bladenightapp.routes;

import de.greencity.bladenightapp.routes.Route.LatLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RouteGpxLoader implements RouteLoader {
    public RouteGpxLoader() {
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

    private boolean loadFromStreamAndClose(InputStream inputStream) {
        boolean result = loadFromOpenStream(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            // Not much to do...
        }
        return result;
    }


    private boolean loadFromOpenStream(InputStream inputStream) {
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
            doc = documentBuilder.parse(inputStream);
        } catch (SAXException e) {
            getLog().error("Could not read XML from stream (malformed?)", e);
            return false;
        } catch (IOException e) {
            getLog().error("Could not read XML from stream (access problem?)", e);
            return false;
        }
        doc.getDocumentElement().normalize();

        nodesLatLong = new ArrayList<LatLong>();;

        NodeList nList = doc.getElementsByTagName("trkpt");
        for (int i = 0 ; i < nList.getLength() ; i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                double longitude = Double.parseDouble(element.getAttribute("lon"));
                double latitude = Double.parseDouble(element.getAttribute("lat"));
                nodesLatLong.add(new LatLong(latitude, longitude));
            }
        }
        return true;
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }

    private static Log log;

    public static void setLog(Log log) {
        RouteGpxLoader.log = log;
    }

    protected static Log getLog() {
        if (log == null)
            setLog(LogFactory.getLog(RouteGpxLoader.class));
        return log;
    }

    private List<LatLong> nodesLatLong;

}
