package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RelationshipInputMessage {
	public RelationshipInputMessage(String deviceId, long friendId, long requestId) {
		this.did = deviceId;
		this.fid = friendId;
		this.req = requestId;
	}
	public String getDeviceId() {
		return did;
	}
	public void setDeviceId(String deviceId) {
		this.did = deviceId;
	}
	public long getRequestId() {
		return req;
	}
	public void setRequestId(long requestId) {
		this.req = requestId;
	}
	public long getFriendId() {
		return fid;
	}
	public void getFriendId(long friendId) {
		this.fid = friendId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public String did;
	public long fid;
	public long req;
}
