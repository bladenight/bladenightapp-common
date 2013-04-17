package de.greencity.bladenightapp.network.messages;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.greencity.bladenightapp.procession.MovingPoint;

public class RealTimeUpdateData {
	public RealTimeUpdateData() {
		hea = new NetMovingPoint();
		tai = new NetMovingPoint();
		up = new NetMovingPoint();
		fri = new ConcurrentHashMap<Integer, NetMovingPoint>();
	}

	public NetMovingPoint getUser() {
		return up;
	}

	public void setUser(NetMovingPoint mp) {
		this.up = mp;
	}


	public long getUserPosition() {
		return up.getPosition();
	}

	public long getUserSpeed() {
		return up.getSpeed();
	}


	public void setUserPosition(long position, long speed) {
		up.setPosition(position);
		up.setSpeed(speed);
	}

	public void isUserOnRoute(boolean isOnRoute) {
		up.isOnRoute(isOnRoute);
	}

	public boolean isUserOnRoute() {
		return up.isOnRoute();
	}


	public void isUserInProcession(boolean isOnProcession) {
		up.isInProcession(isOnProcession);
	}


	public NetMovingPoint getHead() {
		return hea;
	}

	public double getHeadPosition() {
		return hea.getPosition();
	}

	public void invalidateHead() {
		hea.isOnRoute(false);
		hea.isInProcession(false);
	}
	
	public void setHead(MovingPoint mp) {
		hea.copyFrom(mp);
	}

	public void setHead(NetMovingPoint mp) {
		hea = mp;
	}


	public void invalidateTail() {
		hea.isOnRoute(false);
		hea.isInProcession(false);
	}

	public NetMovingPoint getTail() {
		return tai;
	}

	public double getTailPosition() {
		return tai.getPosition();
	}

	public void setTail(MovingPoint mp) {
		tai.copyFrom(mp);
	}

	public void setTail(NetMovingPoint mp) {
		tai = mp;
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

	public Map<Integer, NetMovingPoint> getFriendsMap() {
		return this.fri;
	}

	public void addFriend(int friendId, NetMovingPoint nmp) {
		this.fri.put(friendId, nmp);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public NetMovingPoint hea; 				// Head
	public NetMovingPoint tai; 				// Tail
	public Map<Integer, NetMovingPoint> fri;  	// Friends
	public NetMovingPoint up;  				// User position

	public double rle; 	// Route length
	public String rna; 	// Route name
	public int ust; 		// Total number of clients connected
	public int usr; 		// Total number of clients on the route
}
