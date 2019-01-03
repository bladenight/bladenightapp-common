package de.greencity.bladenightapp.relationships;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.impl.NoOpLog;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.greencity.bladenightapp.exceptions.BadStateException;
import de.greencity.bladenightapp.persistence.InconsistencyException;
import de.greencity.bladenightapp.persistence.ListPersistor;
import de.greencity.bladenightapp.time.ControlledClock;

public class RelationshipStoreTest {

    @Before
    public void before() {
        RelationshipStore.setLog(new NoOpLog());
    }

    @Test
    public void testIds() {
        RelationshipStore store = new RelationshipStore();

        assertEquals(1000, store.pow(10,3));

        store.setRequestIdLength(4);
        for(long i=0; i < 10000; i++) {
            long id = store.generateRequestId();
            assertTrue(id >= 1000);
            assertTrue(id <= 9999);
        }

    }

    @Test
    public void testRequestIdUniqueness() {
        RelationshipStore store = new RelationshipStore();

        Map<Long, Integer> map = new HashMap<Long, Integer>();
        store.setRequestIdLength(4);
        for(long i=0; i < 500 ; i++) {
            HandshakeInfo handshakeInfo = store.newRequest(UUID.randomUUID().toString(), 42);
            long requestId = handshakeInfo.getRequestId();
            assertFalse("Ids shall be given only once " + i, map.containsKey(requestId));
            map.put(requestId, 1);
        }

    }

    @Test
    public void testRelationshipIdUniqueness() {
        RelationshipStore store = new RelationshipStore();

        Map<Long, Integer> map = new HashMap<Long, Integer>();
        store.setRequestIdLength(4);
        for(long i=0; i < 500 ; i++) {
            HandshakeInfo handshakeInfo = store.newRequest(UUID.randomUUID().toString(), 42);
            long relationshipId = store.getRelationshipForRequestId(handshakeInfo.getRequestId()).getId();
            assertFalse("Ids shall be given only once " + relationshipId, map.containsKey(relationshipId));
            map.put(relationshipId, 1);
        }

    }

    @Test
    public void startRelation() {
        RelationshipStore store = new RelationshipStore();
        String deviceId1 = UUID.randomUUID().toString();
        int friendId1 = 42;
        HandshakeInfo handshakeInfo = store.newRequest(deviceId1, friendId1);
        assertTrue(handshakeInfo.getRequestId() > 0);
        assertEquals(friendId1, handshakeInfo.getFriendId());
        assertEquals(true, store.isPendingRequestId(handshakeInfo.getRequestId()));
    }

    @Test
    public void finalizeRelation() throws BadStateException {
        RelationshipStore store = new RelationshipStore();
        String deviceId1 = UUID.randomUUID().toString();
        int friendId1 = 42;
        String deviceId2 = UUID.randomUUID().toString();
        int friendId2 = 142;

        HandshakeInfo handshakeInfo = store.newRequest(deviceId1, friendId1);
        long relationshipId = handshakeInfo.getRequestId();

        assertTrue(store.isPendingRequestId(relationshipId));

        assertFalse(store.exists(deviceId1, deviceId2));
        assertFalse(store.exists(deviceId2, deviceId1));

        assertEquals(0, store.getFinalizedRelationships(deviceId1).size());
        assertEquals(0, store.getFinalizedRelationships(deviceId2).size());

        handshakeInfo = store.finalize(relationshipId, deviceId2, friendId2);
        assertEquals(friendId2, handshakeInfo.getFriendId());

        assertFalse(store.isPendingRequestId(relationshipId));
        assertTrue(store.exists(deviceId1, deviceId2));
        assertTrue(store.exists(deviceId2, deviceId1));

        {
            List<RelationshipMember> list1 = store.getFinalizedRelationships(deviceId1);
            assertEquals(1, list1.size());
            assertEquals(deviceId2, list1.get(0).getDeviceId());
            assertEquals(friendId1, list1.get(0).getFriendId());
        }
        {
            List<RelationshipMember> list2 = store.getFinalizedRelationships(deviceId2);
            assertEquals(1, list2.size());
            assertEquals(deviceId1, list2.get(0).getDeviceId());
            assertEquals(friendId2, list2.get(0).getFriendId());
        }

        // make sure existing relationship gets deleted in case of conflicting ids
        handshakeInfo = store.newRequest(deviceId1, friendId1);
        assertFalse(store.exists(deviceId1, deviceId2));
    }

    @Test
    public void multipleRelations() throws BadStateException {
        RelationshipStore store = new RelationshipStore();
        String deviceId1 = UUID.randomUUID().toString();
        int friendId1_2 = 42;
        int friendId1_3 = 43;
        String deviceId2 = UUID.randomUUID().toString();
        String deviceId3 = UUID.randomUUID().toString();

        HandshakeInfo handshakeInfo;

        handshakeInfo = store.newRequest(deviceId1, friendId1_2);
        handshakeInfo = store.finalize(handshakeInfo.getRequestId(), deviceId2, 42);

        handshakeInfo = store.newRequest(deviceId1, friendId1_3);
        handshakeInfo = store.finalize(handshakeInfo.getRequestId(), deviceId3, 42);

        List<RelationshipMember> list = store.getFinalizedRelationships(deviceId1);

        assertEquals(2, list.size());

        assertEquals(deviceId2, list.get(0).getDeviceId());
        assertEquals(friendId1_2, list.get(0).getFriendId());

        assertEquals(deviceId3, list.get(1).getDeviceId());
        assertEquals(friendId1_3, list.get(1).getFriendId());
    }

    @Test(expected=BadStateException.class)
    public void duplicateFinalization() throws BadStateException {
        RelationshipStore store = new RelationshipStore();
        String deviceId1 = UUID.randomUUID().toString();
        String deviceId2 = UUID.randomUUID().toString();

        HandshakeInfo handshakeInfo = store.newRequest(deviceId1, 42);
        long relationshipId = handshakeInfo.getRequestId();
        store.finalize(relationshipId, deviceId2, 142);
        store.finalize(relationshipId, deviceId2, 142);
    }

    @Test(expected=BadStateException.class)
    public void invalidFinalization() throws BadStateException {
        RelationshipStore store = new RelationshipStore();
        String deviceId1 = UUID.randomUUID().toString();
        String deviceId2 = UUID.randomUUID().toString();
        HandshakeInfo handshakeInfo = store.newRequest(deviceId1, 42);
        long relationshipId = handshakeInfo.getRequestId();
        store.finalize(relationshipId+1, deviceId2, 142);
    }

    @Test(expected=BadStateException.class)
    public void selfRelationship() throws BadStateException {
        RelationshipStore store = new RelationshipStore();
        String deviceId1 = UUID.randomUUID().toString();
        HandshakeInfo handshakeInfo = store.newRequest(deviceId1, 42);
        long relationshipId = handshakeInfo.getRequestId();
        store.finalize(relationshipId, deviceId1, 142);
    }

    @Test(expected=BadStateException.class)
    public void duplicateRelationship() throws BadStateException {
        RelationshipStore store = new RelationshipStore();
        String deviceId1 = UUID.randomUUID().toString();
        String deviceId2 = UUID.randomUUID().toString();
        for ( int i = 0; i < 2 ; i++) {
            HandshakeInfo handshakeInfo = store.newRequest(deviceId1, 100*i+42);
            long relationshipId = handshakeInfo.getRequestId();
            store.finalize(relationshipId, deviceId2, 100*i+43);
            assertEquals(1, store.getFinalizedRelationships(deviceId1).size());
            assertEquals(1, store.getFinalizedRelationships(deviceId2).size());
        }
    }


    @Test
    public void readWrite() throws IOException, BadStateException, InconsistencyException {
        File persistenceDirectory = FileUtils.toFile(RelationshipStoreTest.class.getResource("/de.greencity.bladenightapp.relationships/store1"));
        assertNotNull(persistenceDirectory);

        RelationshipStore store = new RelationshipStore();
        ListPersistor<Relationship> persistorRead = new ListPersistor<Relationship>(Relationship.class, persistenceDirectory);
        store.setPersistor(persistorRead);
        store.read();

        assertExpectedDataInStore(store);

        File cloneDir = folder.newFolder("readWrite");

        ListPersistor<Relationship> persistorWrite = new ListPersistor<Relationship>(Relationship.class, cloneDir);
        store.setPersistor(persistorWrite);
        store.write();

        RelationshipStore storeCheck = new RelationshipStore();
        ListPersistor<Relationship> persistorCheck = new ListPersistor<Relationship>(Relationship.class, cloneDir);
        storeCheck.setPersistor(persistorCheck);
        storeCheck.read();

        assertExpectedDataInStore(storeCheck);
    }

    @Test
    public void removeOutdatedRelationships()  {
        RelationshipStore store = new RelationshipStore();
        ControlledClock clock = new ControlledClock(0);
        long id1 = 11;
        Relationship relationship1 = new Relationship(clock);
        relationship1.setId(id1);
        assertTrue(relationship1.isPending());

        long id2 = 12;
        Relationship relationship2 = new Relationship(clock);
        relationship2.setId(id2);
        relationship2.setDeviceId2("SET");
        assertTrue(! relationship2.isPending());

        clock.set(1000);
        assertEquals(1000, relationship1.getAge());
        clock.set(2000);

        Relationship relationship3 = new Relationship(clock);
        long id3 = 13;
        relationship3.setId(id3);
        assertEquals(2000, relationship1.getAge());
        assertEquals(0, relationship3.getAge());

        store.addRelationship(relationship1);
        store.addRelationship(relationship2);
        store.addRelationship(relationship3);
        int hits = store.removePendingRelationshipsOlderThan(1000);
        assertTrue(store.getRelationshipWithId(id1) == null);
        assertTrue(store.getRelationshipWithId(id2) != null);
        assertTrue(store.getRelationshipWithId(id3) != null);
        assertEquals(1, hits);
    }

    void assertExpectedDataInStore(RelationshipStore store) throws BadStateException {
        assertTrue(store.exists("existing-device-1", "existing-device-2"));

        long requestId = 885989;

        assertTrue(store.isPendingRequestId(requestId));
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
}
