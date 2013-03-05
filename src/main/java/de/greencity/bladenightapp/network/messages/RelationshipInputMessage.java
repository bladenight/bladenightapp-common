package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RelationshipInputMessage {
	public RelationshipInputMessage(String deviceId1, String deviceId2, long requestId) {
		this.did1 = deviceId1;
		this.did2 = deviceId2;
		this.req = requestId;
	}
	public String getDeviceId1() {
		return did1;
	}
	public void setDeviceId1(String deviceId) {
		this.did1 = deviceId;
	}
	public String getDeviceId2() {
		return did2;
	}
	public void setDeviceId2(String deviceId) {
		this.did2 = deviceId;
	}
	public long getRequestId() {
		return req;
	}
	public void setRequestId(long requestId) {
		this.req = requestId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private String did1;
	private String did2;
	private long req;
}
