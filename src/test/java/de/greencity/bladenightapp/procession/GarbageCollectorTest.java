package de.greencity.bladenightapp.procession;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.impl.NoOpLog;
import org.junit.BeforeClass;
import org.junit.Test;

public class GarbageCollectorTest {

	@BeforeClass
	static public void init() {
		GarbageCollector.setLog(new NoOpLog());
	}
	
	@Test
	public void singleParticipant() {
		long t0 = System.currentTimeMillis();
		Map<String, Participant> participants = new HashMap<String, Participant>();

		Participant p1 = new Participant();
		p1.setLastLifeSign(t0);
		participants.put("1", p1);
		
		new GarbageCollector(participants.values()).collect();
		
		assertNotNull("A single participant shall never be removed", participants.get("1"));
	}

	@Test
	public void twoRecentParticipants() {
		long t0 = System.currentTimeMillis();
		Map<String, Participant> participants = new HashMap<String, Participant>();

		Participant p1 = new Participant();
		p1.setLastLifeSign(t0-1000);
		participants.put("1", p1);

		Participant p2 = new Participant();
		p1.setLastLifeSign(t0-2000);
		participants.put("2", p2);

		new GarbageCollector(participants.values()).collect();
		
		assertNotNull("Participant shall not be removed", participants.get("1"));
		assertNotNull("Participant shall not be removed", participants.get("2"));
	}

	@Test
	public void twoParticipantsInTheFuture() {
		long t0 = System.currentTimeMillis();
		Map<String, Participant> participants = new HashMap<String, Participant>();

		Participant p1 = new Participant();
		p1.setLastLifeSign(t0+100000);
		participants.put("1", p1);

		Participant p2 = new Participant();
		p1.setLastLifeSign(t0+150000);
		participants.put("2", p2);

		new GarbageCollector(participants.values()).collect();
		
		assertNotNull("Participant shall not be removed", participants.get("1"));
		assertNotNull("Participant shall not be removed", participants.get("2"));
	}

	@Test
	public void twoParticipantsWithOldOne() {
		long unit = 10000;
		long t0 = System.currentTimeMillis() - 6 * unit;

		Map<String, Participant> participants = new HashMap<String, Participant>();

		Participant p1 = new Participant();
		p1.setLastLifeSign(t0);
		participants.put("1", p1);

		Participant p2 = new Participant();
		p2.setLastLifeSign(t0 + 5 * unit);
		participants.put("2", p2);

		Participant p3 = new Participant();
		p3.setLastLifeSign(t0 + 5 * unit);
		participants.put("3", p3);

		new GarbageCollector(participants.values(), 2.0).collect();
		
		assertTrue("Participant shall be removed", participants.get("1") == null);
		assertNotNull("Participant shall not be removed", participants.get("2"));
		assertNotNull("Participant shall not be removed", participants.get("3"));
	}
}

