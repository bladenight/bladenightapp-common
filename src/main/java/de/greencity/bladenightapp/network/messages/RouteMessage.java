package de.greencity.bladenightapp.network.messages;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import de.greencity.bladenightapp.routes.Route;

@JsonAutoDetect(fieldVisibility=Visibility.ANY,getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class RouteMessage {
	public LatLong[] nod; // node list
	public int len;
	public String nam;

	public RouteMessage() {
		nod = new LatLong[0];
		nam = "undefined route name";
	}

	public RouteMessage(Route r) {
		copyFromRoute(r);
	}

	public static RouteMessage newFromRoute(Route r) {
		RouteMessage routeMessage = new RouteMessage();
		routeMessage.copyFromRoute(r);
		return routeMessage;
	}

	public void copyFromRoute(Route r) {
		len = (int) r.getLength();
		nod = new LatLong[r.getNumberOfSegments()+1];
		int i = 0;
		for (Route.LatLong rll : r.getNodesLatLong() ) {
			nod[i++] = new LatLong(rll.lat, rll.lon);
		}
		setRouteName(r.getName());
	}
	
	public List<LatLong> getNodes() {
		return Arrays.asList(nod);
	}

	public String getRouteName() {
		return nam;
	}


	public int getRouteLength() {
		return len;
	}

	public void setRouteLength(int length) {
		this.len = length;
	}

	public void setRouteName(String routeName) {
		this.nam = routeName;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
