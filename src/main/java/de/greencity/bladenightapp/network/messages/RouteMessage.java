package de.greencity.bladenightapp.network.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.greencity.bladenightapp.routes.Route;

public class RouteMessage {
	private LatLong[] nod; // node list
	private int len;

	public RouteMessage() {
		nod = new LatLong[0];
	}

	public RouteMessage(Route r) {
		fromRoute(r);
	}

	void fromRoute(Route r) {
		len = (int) r.getLength();
		nod = new LatLong[r.getNumberOfSegments()+1];
		int i = 0;
		for (Route.LatLong rll : r.getNodesLatLong() ) {
			nod[i++] = new LatLong(rll.lat, rll.lon);
		}
	}
	
	public List<LatLong> getNodes() {
		return Arrays.asList(nod);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	protected final static DateTimeFormatter dateFormatter;

	static {
		dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
	}
}
