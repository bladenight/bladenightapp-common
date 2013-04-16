package de.greencity.bladenightapp.relationships;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RelationshipMember {
	RelationshipMember(int friendId, String deviceId) {
		this.friendId = friendId;
		this.deviceId = deviceId;
	}
	public int getFriendId() {
		return friendId;
	}
	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private int friendId;
	private String deviceId;
}