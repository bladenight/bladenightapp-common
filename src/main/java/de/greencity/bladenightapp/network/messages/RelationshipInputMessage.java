package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY,getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
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
	
	public String did1;
	public String did2;
	public long req;
}
