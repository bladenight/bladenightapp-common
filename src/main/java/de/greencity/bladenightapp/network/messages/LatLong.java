package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY,getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class LatLong {
	LatLong() {
	}

	public LatLong(double lat, double lon) {
		setLatitude(lat);
		setLongitude(lon);
	}

	public double getLatitude() {
		return la;
	}

	public void setLatitude(double la) {
		this.la = convertToInternal(la);
	}

	public double getLongitude() {
		return lo;
	}

	public void setLongitude(double lo) {
		this.lo = convertToInternal(lo);
	}
	
	private double convertToInternal(double d) {
		return Math.round(d * precision) / precision;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, false);
	}
	
	public double la, lo;
	final static public double precision = 10000;
}
