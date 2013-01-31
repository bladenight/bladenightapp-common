package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class LatLong {
	LatLong() {
	}

	public LatLong(double lat, double lon) {
		la = lat;
		lo = lon;
	}

	public double getLatitude() {
		return la;
	}

	public void setLatitude(double la) {
		this.la = la;
	}

	public double getLongitude() {
		return lo;
	}

	public void setLongitude(double lo) {
		this.lo = lo;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

	private double la, lo;

}
