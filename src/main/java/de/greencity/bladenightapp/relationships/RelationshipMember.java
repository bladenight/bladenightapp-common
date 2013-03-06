package de.greencity.bladenightapp.relationships;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RelationshipMember {
	RelationshipMember(long friendId, String deviceId) {
		this.friendId = friendId;
		this.deviceId = deviceId;
	}
	public long getFriendId() {
		return friendId;
	}
	public void setFriendId(long friendId) {
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

	private long friendId;
	private String deviceId;
}