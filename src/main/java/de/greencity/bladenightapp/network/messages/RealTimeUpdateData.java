package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RealTimeUpdateData {
	public RealTimeUpdateData() {
		hea = new PointOnRoute();
		tai = new PointOnRoute();
	}

	public PointOnRoute getHead() {
		return hea;
	}

	public void setHead(double position, double speed) {
		hea.setPosition(position);
		hea.setSpeed(0);
	}

	public PointOnRoute getTai() {
		return tai;
	}

	public void setTail(double position, double speed) {
		tai.setPosition(position);
		tai.setSpeed(0);
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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private PointOnRoute hea; // Head
	private PointOnRoute tai; // Tail
	private int ust; // Total number of clients connected
	private int usr; // Total number of clients on the route
}
