package de.greencity.bladenightapp.network.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class FriendMessage extends MovingPointMessage {

	public long getRequestId() {
		return req;
	}

	public void setRequestId(long req) {
		this.req = req;
	}
	
	public int getFriendId() {
		return fid;
	}

	public void setFriendId(int fid) {
		this.fid = fid;
	}

	public boolean isOnline() {
		return onl;
	}

	public void isOnline(boolean onl) {
		this.onl = onl;
	}
	
	public boolean isRelationshipPending() {
		return req != 0;
	}

	private long req;
	private int fid;
	private boolean onl;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
