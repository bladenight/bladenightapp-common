package de.greencity.bladenightapp.network.messages;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class FriendsMessage {
	public FriendsMessage() {
		fri = new ConcurrentHashMap<Integer, FriendMessage>();
	}
	public void clear() {
		fri.clear();
	}

	public boolean containsKey(Object arg0) {
		return fri.containsKey(arg0);
	}

	public boolean containsValue(Object arg0) {
		return fri.containsValue(arg0);
	}

	public Set<Entry<Integer, FriendMessage>> entrySet() {
		return fri.entrySet();
	}

	public boolean equals(Object arg0) {
		return fri.equals(arg0);
	}

	public FriendMessage get(Object arg0) {
		return fri.get(arg0);
	}

	public int hashCode() {
		return fri.hashCode();
	}

	public boolean isEmpty() {
		return fri.isEmpty();
	}

	public Set<Integer> keySet() {
		return fri.keySet();
	}

	public FriendMessage put(Integer arg0, FriendMessage arg1) {
		return fri.put(arg0, arg1);
	}

	public void putAll(Map<? extends Integer, ? extends FriendMessage> arg0) {
		fri.putAll(arg0);
	}

	public FriendMessage remove(Object arg0) {
		return fri.remove(arg0);
	}

	public int size() {
		return fri.size();
	}

	public Collection<FriendMessage> values() {
		return fri.values();
	}
	private Map<Integer, FriendMessage> fri;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
