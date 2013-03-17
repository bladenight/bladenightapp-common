package de.greencity.bladenightapp.procession;


public class GarbageCollectorTest {
//
//	@BeforeClass
//	static public void init() {
//		GarbageCollector.setLog(new NoOpLog());
//	}
//	
//	@Test
//	public void singleParticipant() {
//		long t0 = System.currentTimeMillis();
//		Procession procession = new Procession();
//		ControlledClock clock = new ControlledClock();
//		procession.setClock(clock);
//	
//		ParticipantInput participantInput = new ParticipantInput("1", true, 0, 0); 
//		Participant p1 = new Participant();
//		p1.setLastLifeSign(t0);
//		procession.updateParticipant(participantInput);
//		
//		new GarbageCollector(participants.values()).collect();
//		
//		assertNotNull("A single participant shall never be removed", participants.get("1"));
//	}
//
//	@Test
//	public void twoRecentParticipants() {
//		long t0 = System.currentTimeMillis();
//		Map<String, Participant> participants = new HashMap<String, Participant>();
//
//		Participant p1 = new Participant();
//		p1.setLastLifeSign(t0-1000);
//		participants.put("1", p1);
//
//		Participant p2 = new Participant();
//		p1.setLastLifeSign(t0-2000);
//		participants.put("2", p2);
//
//		new GarbageCollector(participants.values()).collect();
//		
//		assertNotNull("Participant shall not be removed", participants.get("1"));
//		assertNotNull("Participant shall not be removed", participants.get("2"));
//	}
//
//	@Test
//	public void twoParticipantsInTheFuture() {
//		long t0 = System.currentTimeMillis();
//		Map<String, Participant> participants = new HashMap<String, Participant>();
//
//		Participant p1 = new Participant();
//		p1.setLastLifeSign(t0+100000);
//		participants.put("1", p1);
//
//		Participant p2 = new Participant();
//		p1.setLastLifeSign(t0+150000);
//		participants.put("2", p2);
//
//		new GarbageCollector(participants.values()).collect();
//		
//		assertNotNull("Participant shall not be removed", participants.get("1"));
//		assertNotNull("Participant shall not be removed", participants.get("2"));
//	}
//
//	@Test
//	public void twoParticipantsWithOldOne() {
//		long unit = 10000;
//		long t0 = System.currentTimeMillis() - 6 * unit;
//
//		Map<String, Participant> participants = new HashMap<String, Participant>();
//
//		Participant p1 = new Participant();
//		p1.setLastLifeSign(t0);
//		participants.put("1", p1);
//
//		Participant p2 = new Participant();
//		p2.setLastLifeSign(t0 + 5 * unit);
//		participants.put("2", p2);
//
//		Participant p3 = new Participant();
//		p3.setLastLifeSign(t0 + 5 * unit);
//		participants.put("3", p3);
//
//		new GarbageCollector(participants.values(), 2.0).collect();
//		
//		assertTrue("Participant shall be removed", participants.get("1") == null);
//		assertNotNull("Participant shall not be removed", participants.get("2"));
//		assertNotNull("Participant shall not be removed", participants.get("3"));
//	}
//
////	@Test
////	public void removeOutdatedParticipant() {
////		String id1 = addParticipant(48.135607, 11.524631);
////		String id2 = addParticipant(48.139625, 11.518710);
////		String id3 = addParticipant(48.139625, 11.518710);
////		String id4 = addParticipant(48.139625, 11.518710);
////		String id5 = addParticipant(48.139625, 11.518710);
////		assertEquals(5, procession.getParticipantCount());
////		long now = System.currentTimeMillis();
////		procession.getParticipant(id1).setLastLifeSign(now-1000);
////		procession.getParticipant(id2).setLastLifeSign(now-100000);
////		procession.getParticipant(id3).setLastLifeSign(now-2000);
////		procession.getParticipant(id4).setLastLifeSign(now-3000);
////		procession.getParticipant(id5).setLastLifeSign(now-2500);
////		
////		procession.removeOutdatedParticipants(5.0);
////		assertEquals(4, procession.getParticipantCount());
////		assertTrue(procession.getParticipant(id1) != null);
////		assertTrue(procession.getParticipant(id2) == null);
////		assertTrue(procession.getParticipant(id3) != null);
////		assertTrue(procession.getParticipant(id4) != null);
////		assertTrue(procession.getParticipant(id5) != null);
////	}
//
}

