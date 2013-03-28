package de.greencity.bladenightapp.network.messages;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY,getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class RelationshipOutputMessage {
	public RelationshipOutputMessage(long requestId, long friendId) {
		rid = requestId;
		fid = friendId;
	}
	public long getFriendId() {
		return fid;
	}
	public void setFriendId(long fid) {
		this.fid = fid;
	}
	public long getRequestId() {
		return rid;
	}
	public void setRequestId(long rid) {
		this.rid = rid;
	}
	public long fid;
	public long rid;
}
