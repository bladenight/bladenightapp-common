package de.greencity.bladenightapp.relationships;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.greencity.bladenightapp.exceptions.BadStateException;

public class RelationshipStore {

	public RelationshipStore() {
		pending = new ConcurrentHashMap<Long, Relationship>();
		finalized = Collections.synchronizedList(new ArrayList<Relationship>());
		lock = new Object();
	}

	public static RelationshipStore newFromFile(File file) throws IOException {
		String json = FileUtils.readFileToString(file);
		return new Gson().fromJson(json, RelationshipStore.class);
	}

	public synchronized void write(File file) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(this);
		FileUtils.writeStringToFile(file, json);
	}


	public HandshakeInfo newRequest(String deviceId1) {
		synchronized (lock) {
			long requestId = generateRequestId();
			long friendId = generateFriendId(deviceId1);

			Relationship relationship = new Relationship(deviceId1);
			relationship.setFriendId1(friendId);
			pending.put(requestId, relationship);

			getLog().info("Created new request for " + deviceId1 + " " + requestId + " = " + relationship);

			HandshakeInfo handshakeInfo = new HandshakeInfo();
			handshakeInfo.setRequestId(requestId);
			handshakeInfo.setFriendId(friendId);

			return handshakeInfo;
		}
	}

	public synchronized HandshakeInfo finalize(long requestId, String deviceId2) throws BadStateException, TimeoutException {
		Relationship rel = pending.get(requestId);

		finalizeCheck(requestId, deviceId2, rel);

		HandshakeInfo handshakeInfo = new HandshakeInfo();
		synchronized (lock) {
			long friendId = generateFriendId(deviceId2);

			handshakeInfo.setFriendId(friendId);

			rel.setDeviceId2(deviceId2);
			rel.setFriendId2(friendId);
			
			pending.remove(requestId);
			finalized.add(rel);
			
			getLog().info(rel.getDeviceId1() + " and " + rel.getDeviceId2() + " are now connected");
		}
		return handshakeInfo;
	}

	private long generateFriendId(String deviceId) {
		long count=1;
		synchronized (lock) {
			for (Relationship relationship : pending.values() ) {
				if ( relationship.involves(deviceId) )
					count++;
			}
			for (Relationship relationship : finalized ) {
				if ( relationship.involves(deviceId) )
					count++;
			}
		}
		return count;
	}

	private void finalizeCheck(long requestId, String deviceId2, Relationship rel) throws BadStateException, TimeoutException {
		if ( rel == null ) {
			String msg = "Not a valid pending relationship id: " + requestId;
			getLog().warn(msg);
			throw new BadStateException(msg);
		}
		if ( rel.getDeviceId1() == deviceId2 ) {
			String msg = "Relationship with self is not allowed: "+rel;
			getLog().warn(msg);
			throw new BadStateException(msg);
		}
		if ( exists(rel.getDeviceId1(), deviceId2) ) {
			String msg = "Devices are already connected: "+rel;
			getLog().warn(msg);
			throw new BadStateException(msg);
		}
		if ( requestTimeout > 0 && rel.getAge() > requestTimeout ) {
			pending.remove(requestId);
			String msg = "Relationship request has timed out: "+rel;
			getLog().warn(msg);
			throw new TimeoutException(msg);
		}
	}


	public void setIdLength(int digits) {
		idsLength = digits; 
	}

	public void setRequestTimeOut(long timeout) {
		this.requestTimeout = timeout;
	}

	synchronized long generateRequestId() {
		while(true) {
			long id = generateRequestIdCandidate();
			if ( ! pending.containsKey(id)  )
				return id;
		}
	}

	private long generateRequestIdCandidate() {
		long min = pow(10, idsLength-1); 
		long range = pow(10, idsLength) - min;
		return  min + ( Math.abs(getRandom().nextLong()) % range );
	}

	long pow(int a, int b) {
		if ( b <= 0)
			return 1;
		long result = a;
		for ( int i = 0; i < b-1 ; i++)
			result = result * a;
		return result;
	}

	public boolean exists(String deviceId1, String deviceId2) {
		for (Relationship relationship : finalized ) {
			int match = 0;
			if ( deviceId1.equals(relationship.getDeviceId1()) )
				match++;
			if ( deviceId1.equals(relationship.getDeviceId2()) )
				match++;
			if ( deviceId2.equals(relationship.getDeviceId1()) )
				match++;
			if ( deviceId2.equals(relationship.getDeviceId2()) )
				match++;
			if ( match == 2)
				return true;
		}
		return false;
	}

	public boolean isPending(long requestId) {
		Relationship rel = pending.get(requestId);
		return ( rel != null );
	}

	/** Returns the deviceId's that have a relationship to the given one
	 */
	public List<RelationshipMember> getRelationships(String deviceId) {
		List<RelationshipMember> list = new ArrayList<RelationshipMember>();
		for (Relationship relationship : finalized ) {
			if ( ! relationship.isPending() ) {
				if ( deviceId.equals(relationship.getDeviceId1()))
					list.add(new RelationshipMember(relationship.getFriendId1(), relationship.getDeviceId2()));
				if ( deviceId.equals(relationship.getDeviceId2()))
					list.add(new RelationshipMember(relationship.getFriendId2(), relationship.getDeviceId1()));
			}
		}
		return list;
	}

	private Random getRandom() {
		if ( random == null)
			random = new Random();
		return random;
	}

	private static Log log;

	public static void setLog(Log log) {
		RelationshipStore.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(RelationshipStore.class));
		return log;
	}

	private Map<Long, Relationship> pending;
	private List<Relationship> finalized;
	private transient Random random = null;
	private int idsLength = 6;
	private long requestTimeout = 5 * 60 * 1000; // ms
	private Object lock;
}
