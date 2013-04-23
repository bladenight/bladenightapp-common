package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.greencity.bladenightapp.procession.MovingPoint;

public class RealTimeUpdateData {
	public RealTimeUpdateData() {
		hea = new MovingPointMessage();
		tai = new MovingPointMessage();
		up = new MovingPointMessage();
		fri = new FriendsMessage();
	}

	public MovingPointMessage getUser() {
		return up;
	}

	public void setUser(MovingPointMessage mp) {
		this.up = mp;
	}


	public long getUserPosition() {
		return up.getPosition();
	}

	public int getUserAccuracy() {
		return up.getAccuracy();
	}

	public void setUserAccuracy(int accuracy) {
		up.setAccuracy(accuracy);
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


	public MovingPointMessage getHead() {
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

	public void setHead(MovingPointMessage mp) {
		hea = mp;
	}


	public void invalidateTail() {
		hea.isOnRoute(false);
		hea.isInProcession(false);
	}

	public MovingPointMessage getTail() {
		return tai;
	}

	public double getTailPosition() {
		return tai.getPosition();
	}

	public void setTail(MovingPoint mp) {
		tai.copyFrom(mp);
	}

	public void setTail(MovingPointMessage mp) {
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

	public FriendsMessage getFriends() {
		return this.fri;
	}

	public void addFriend(int friendId, FriendMessage friend) {
		this.fri.put(friendId, friend);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public MovingPointMessage hea; 				// Head
	public MovingPointMessage tai; 				// Tail
	public FriendsMessage fri;  	// Friends
	public MovingPointMessage up;  				// User position

	public double rle; 	// Route length
	public String rna; 	// Route name
	public int ust; 		// Total number of clients connected
	public int usr; 		// Total number of clients on the route
}
