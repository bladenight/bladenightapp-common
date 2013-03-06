package de.greencity.bladenightapp.relationships;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class HandshakeInfo {
	public long getFriendId() {
		return friendId;
	}
	public void setFriendId(long friendId) {
		this.friendId = friendId;
	}
	public long getRequestId() {
		return requestId;
	}
	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private long friendId;
	private long requestId;
}