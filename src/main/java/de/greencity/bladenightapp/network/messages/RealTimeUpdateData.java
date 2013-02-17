package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RealTimeUpdateData {
	public RealTimeUpdateData() {
		hea = new PointOnRoute();
		tai = new PointOnRoute();
		up = new PointOnRoute();
	}

	public PointOnRoute getUserPosition() {
		return up;
	}

	public void setUserPosition(double position, double speed) {
		up.setPosition(position);
		up.setSpeed(speed);
	}


	public PointOnRoute getHead() {
		return hea;
	}

	public void setHead(double position, double speed) {
		hea.setPosition(position);
		hea.setSpeed(speed);
	}

	public PointOnRoute getTail() {
		return tai;
	}

	public void setTail(double position, double speed) {
		tai.setPosition(position);
		tai.setSpeed(speed);
	}
	
	public int getUserTotal() {
		return ust;
	}

	public void setUserTotal(int count) {
		this.ust = count;
	}

	public int getUserOnRoute() {
		return usr;
	}

	public void setUserOnRoute(int count) {
		this.usr = count;
	}

	public double getRouteLength() {
		return rle;
	}

	public void setRouteLength(double length) {
		this.rle = length;
	}

	public String getRouteName() {
		return rna;
	}

	public void setRouteName(String routeName) {
		this.rna = routeName;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private PointOnRoute hea; // Head
	private PointOnRoute tai; // Tail
	private PointOnRoute up;  // User position

	private double rle; 	// Route length
	private String rna; 	// Route name
	private int ust; 		// Total number of clients connected
	private int usr; 		// Total number of clients on the route
}
