package de.greencity.bladenightapp.relationships;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.greencity.bladenightapp.time.Clock;
import de.greencity.bladenightapp.time.SystemClock;

class Relationship {

	public Relationship(String deviceId1) {
		this.deviceId1 = deviceId1;
		this.creationTimestamp = clock.currentTimeMillis();
	}

	public String getDeviceId1() {
		return deviceId1;
	}

	public String getDeviceId2() {
		return deviceId2;
	}

	public void setDeviceId2(String deviceId2) {
		this.deviceId2 = deviceId2;
	}

	public long getFriendId1() {
		return friendId1;
	}

	public long getFriendId2() {
		return friendId2;
	}

	public void setFriendId1(long friendId) {
		this.friendId1 = friendId;
	}

	public void setFriendId2(long friendId) {
		this.friendId2 = friendId;
	}

	public boolean isPending() {
		return deviceId2 == null;
	}

	public long getAge() {
		return clock.currentTimeMillis() - creationTimestamp;
	}

	public boolean involves(String deviceId) {
		return deviceId.equals(deviceId1) || deviceId.equals(deviceId2); 
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private String deviceId1;
	private long friendId1; 
	private String deviceId2;
	private long friendId2; 
	private long creationTimestamp;
	private Clock clock = new SystemClock();
}
