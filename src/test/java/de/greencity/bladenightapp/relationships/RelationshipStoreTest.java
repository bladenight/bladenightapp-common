package de.greencity.bladenightapp.relationships;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.greencity.bladenightapp.events.EventsList;
import de.greencity.bladenightapp.exceptions.BadStateException;
import de.greencity.bladenightapp.time.Sleep;

public class RelationshipStoreTest {

	@Before
	public void before() {
		RelationshipStore.setLog(new NoOpLog());
	}

	@Test
	public void testIds() {
		RelationshipStore store = new RelationshipStore();

		assertEquals(1000, store.pow(10,3));

		store.setIdLength(4);
		for(long i=0; i < 10000; i++) {
			long id = store.generateUniqId();
			assertTrue(id >= 1000);
			assertTrue(id <= 9999);
		}

	}

	@Test
	public void testIdUniqueness() {
		RelationshipStore store = new RelationshipStore();

		Map<Long, Integer> map = new HashMap<Long, Integer>();
		store.setIdLength(4);
		for(long i=0; i < 1000 ; i++) {
			HandshakeInfo handshakeInfo = store.newRequest(UUID.randomUUID().toString());
			long relationshipId = handshakeInfo.getRequestId();
			assertFalse("Ids shall be given only once " + i, map.containsKey(relationshipId));
			map.put(relationshipId, 1);
		}

	}

	@Test
	public void startRelation() {
		RelationshipStore store = new RelationshipStore();
		String deviceId1 = UUID.randomUUID().toString();
		HandshakeInfo handshakeInfo = store.newRequest(deviceId1);
		assertTrue(handshakeInfo.getRequestId() > 0);
		assertTrue(handshakeInfo.getFriendId() == 1);
		assertEquals(true, store.isPending(handshakeInfo.getRequestId()));
	}

	@Test
	public void finalizeRelation() throws BadStateException, TimeoutException {
		RelationshipStore store = new RelationshipStore();
		String deviceId1 = UUID.randomUUID().toString();
		String deviceId2 = UUID.randomUUID().toString();

		HandshakeInfo handshakeInfo = store.newRequest(deviceId1);
		long relationshipId = handshakeInfo.getRequestId();

		assertTrue(store.isPending(relationshipId));

		assertFalse(store.exists(deviceId1, deviceId2));
		assertFalse(store.exists(deviceId2, deviceId1));

		assertEquals(0, store.getRelationships(deviceId1).size());
		assertEquals(0, store.getRelationships(deviceId2).size());

		handshakeInfo = store.finalize(relationshipId, deviceId2);
		assertEquals(1, handshakeInfo.getFriendId());

		assertFalse(store.isPending(relationshipId));
		assertTrue(store.exists(deviceId1, deviceId2));
		assertTrue(store.exists(deviceId2, deviceId1));

		{
			List<String> list1 = store.getRelationships(deviceId1);
			assertEquals(1, list1.size());
			assertEquals(deviceId2, list1.get(0));
		}
		{
			List<String> list2 = store.getRelationships(deviceId2);
			assertEquals(1, list2.size());
			assertEquals(deviceId1, list2.get(0));
		}

		// make sure the friend id is incremented
		handshakeInfo = store.newRequest(deviceId1);
		assertEquals(2, handshakeInfo.getFriendId());
	}

	@Test(expected=BadStateException.class)
	public void duplicateFinalization() throws BadStateException, TimeoutException {
		RelationshipStore store = new RelationshipStore();
		String deviceId1 = UUID.randomUUID().toString();
		String deviceId2 = UUID.randomUUID().toString();

		HandshakeInfo handshakeInfo = store.newRequest(deviceId1);
		long relationshipId = handshakeInfo.getRequestId();
		store.finalize(relationshipId, deviceId2);
		store.finalize(relationshipId, deviceId2);
	}

	@Test(expected=BadStateException.class)
	public void invalidFinalization() throws BadStateException, TimeoutException {
		RelationshipStore store = new RelationshipStore();
		String deviceId1 = UUID.randomUUID().toString();
		String deviceId2 = UUID.randomUUID().toString();
		HandshakeInfo handshakeInfo = store.newRequest(deviceId1);
		long relationshipId = handshakeInfo.getRequestId();
		store.finalize(relationshipId+1, deviceId2);
	}

	@Test(expected=BadStateException.class)
	public void selfRelationship() throws BadStateException, TimeoutException {
		RelationshipStore store = new RelationshipStore();
		String deviceId1 = UUID.randomUUID().toString();
		HandshakeInfo handshakeInfo = store.newRequest(deviceId1);
		long relationshipId = handshakeInfo.getRequestId();
		store.finalize(relationshipId, deviceId1);
	}

	@Test(expected=TimeoutException.class)
	public void testTimeout() throws InterruptedException, BadStateException, TimeoutException {
		RelationshipStore store = new RelationshipStore();

		store.setRequestTimeOut(1);
		String deviceId1 = UUID.randomUUID().toString();
		String deviceId2 = UUID.randomUUID().toString();
		HandshakeInfo handshakeInfo = store.newRequest(deviceId1);
		long relationshipId = handshakeInfo.getRequestId();

		Sleep.sleep(2);

		store.finalize(relationshipId, deviceId2);
	}

	@Test
	public void readWrite() throws IOException, BadStateException, TimeoutException {
		File file = FileUtils.toFile(EventsList.class.getResource("/de.greencity.bladenightapp.relationships/relationshipstore.json"));

		RelationshipStore store = RelationshipStore.newFromFile(file);

		File tempFile = folder.newFile(this.getClass().getSimpleName()+"-test.json");
		store.write(tempFile);

		verifyReadStore(store);

		RelationshipStore store2 = RelationshipStore.newFromFile(tempFile);
		verifyReadStore(store2);
	}

	void verifyReadStore(RelationshipStore store) throws BadStateException, TimeoutException {
		store.setRequestTimeOut(0);

		assertTrue(store.exists("existing-device-1", "existing-device-2"));

		long requestId = 885989;
		assertTrue(store.isPending(requestId));

		store.finalize(requestId, "pending-device-2");

		assertTrue(store.exists("pending-device-1", "pending-device-2"));
	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
}
