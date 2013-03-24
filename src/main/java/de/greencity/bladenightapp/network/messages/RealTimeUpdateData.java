package de.greencity.bladenightapp.network.messages;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RealTimeUpdateData {
	public RealTimeUpdateData() {
		hea = new PointOnRoute();
		tai = new PointOnRoute();
		up = new PointOnRoute();
		fri = new ConcurrentHashMap<Long, PointOnRoute>();
	}

	public PointOnRoute getUser() {
		return up;
	}

	public double getUserPosition() {
		return up.getPosition();
	}

	public double getUserSpeed() {
		return up.getSpeed();
	}


	public void setUserPosition(double position, double speed) {
		up.setPosition(position);
		up.setSpeed(speed);
	}


	public PointOnRoute getHead() {
		return hea;
	}

	public double getHeadPosition() {
		return hea.getPosition();
	}

	public void setHead(double position, double speed) {
		hea.setPosition(position);
		hea.setSpeed(speed);
	}

	public PointOnRoute getTail() {
		return tai;
	}

	public double getTailPosition() {
		return tai.getPosition();
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

	public boolean isUserOnRoute() {
		return onr;
	}

	public void isUserOnRoute(boolean isOnRoute) {
		onr = isOnRoute;
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

	public Map<Long, PointOnRoute> getFriendsMap() {
		return this.fri;
	}

	public void addFriend(long friendId, double linearPosition, double linearSpeed) {
		this.fri.put(friendId, new PointOnRoute(linearPosition, linearSpeed));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public PointOnRoute hea; 				// Head
	public PointOnRoute tai; 				// Tail
	public Map<Long, PointOnRoute> fri;  	// Friends
	public boolean onr; 	  				// Is user on route. If not, "up" data is not relevant
	public PointOnRoute up;  				// User position

	public double rle; 	// Route length
	public String rna; 	// Route name
	public int ust; 		// Total number of clients connected
	public int usr; 		// Total number of clients on the route
}
