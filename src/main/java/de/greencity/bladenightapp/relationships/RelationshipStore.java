package de.greencity.bladenightapp.relationships;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.exceptions.BadStateException;
import de.greencity.bladenightapp.persistence.ListPersistor;

public class RelationshipStore {

	public RelationshipStore() {
		relationships = Collections.synchronizedList(new ArrayList<Relationship>());
		lock = new Object();
	}

	public void read() throws IOException {
		persistor.read();
	}

	public void write() throws IOException {
		persistor.write();
	}

	public void setPersistor(ListPersistor<Relationship> persistor) {
		this.persistor = persistor;
		persistor.setList(relationships);
	}

	public HandshakeInfo newRequest(String deviceId1) {
		synchronized (lock) {
			long requestId = generateRequestId();
			long friendId = generateFriendId(deviceId1);

			Relationship relationship = new Relationship(deviceId1);
			relationship.setFriendId1(friendId);
			relationship.setRequestId(requestId);
			relationships.add(relationship);

			getLog().info("Created new request for " + deviceId1 + " " + requestId + " = " + relationship);

			HandshakeInfo handshakeInfo = new HandshakeInfo();
			handshakeInfo.setRequestId(requestId);
			handshakeInfo.setFriendId(friendId);

			return handshakeInfo;
		}
	}

	public synchronized HandshakeInfo finalize(long requestId, String deviceId2) throws BadStateException, TimeoutException {
		Relationship rel = getRelationshipForRequestId(requestId);

		finalizeCheck(requestId, deviceId2, rel);

		HandshakeInfo handshakeInfo = new HandshakeInfo();
		synchronized (lock) {
			long friendId = generateFriendId(deviceId2);

			handshakeInfo.setFriendId(friendId);

			rel.setRequestId(0);
			rel.setDeviceId2(deviceId2);
			rel.setFriendId2(friendId);
			
			getLog().info(rel.getDeviceId1() + " and " + rel.getDeviceId2() + " are now connected");
		}
		return handshakeInfo;
	}

	public Relationship getRelationshipForRequestId(long requestId) {
		for ( Relationship rel : relationships) {
			if ( rel.getRequestId() == requestId )
				return rel;
		}
		return null;
	}

	private Relationship getRelationshipWithId(long id) {
		for ( Relationship rel : relationships) {
			if ( rel.getId() == id )
				return rel;
		}
		return null;
	}


	private long generateFriendId(String deviceId) {
		long count=1;
		synchronized (lock) {
			for (Relationship relationship : relationships ) {
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
			relationships.remove(rel);
			String msg = "Relationship request has timed out: "+rel;
			getLog().warn(msg);
			throw new TimeoutException(msg);
		}
	}

	public void setRelationshipIdLength(int digits) {
		relationshipIdLength = digits;
	}

	public void setRequestIdLength(int digits) {
		requestIdLength = digits; 
	}

	public void setRequestTimeOut(long timeout) {
		this.requestTimeout = timeout;
	}

	synchronized long generateRequestId() {
		while(true) {
			long id = generateIdCandidate(requestIdLength);
			if ( getRelationshipForRequestId(id) == null )
				return id;
		}
	}

	synchronized long generateRelationshipId() {
		while(true) {
			long id = generateIdCandidate(relationshipIdLength);
			if ( getRelationshipWithId(id) == null )
				return id;
		}
	}


	private long generateIdCandidate(int length) {
		long min = pow(10, length-1); 
		long range = pow(10, length) - min;
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
		for (Relationship relationship : relationships ) {
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

	public boolean isPendingRequestId(long requestId) {
		Relationship rel = getRelationshipForRequestId(requestId);
		return ( rel != null );
	}

	/** Returns the deviceId's that have a relationship to the given one
	 */
	public List<RelationshipMember> getRelationships(String deviceId) {
		List<RelationshipMember> list = new ArrayList<RelationshipMember>();
		for (Relationship relationship : relationships ) {
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
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

	private List<Relationship> relationships;
	private transient Random random = null;
	private int requestIdLength = 6;
	private int relationshipIdLength = 12;
	private long requestTimeout = 5 * 60 * 1000; // ms
	private Object lock;
	private ListPersistor<Relationship> persistor;


}
