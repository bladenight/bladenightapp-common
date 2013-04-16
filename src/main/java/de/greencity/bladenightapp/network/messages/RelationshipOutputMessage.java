package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class RelationshipOutputMessage {
	public RelationshipOutputMessage(long requestId, int friendId) {
		rid = requestId;
		fid = friendId;
	}
	public int getFriendId() {
		return fid;
	}
	public void setFriendId(int fid) {
		this.fid = fid;
	}
	public long getRequestId() {
		return rid;
	}
	public void setRequestId(long rid) {
		this.rid = rid;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public int fid;
	public long rid;
}
