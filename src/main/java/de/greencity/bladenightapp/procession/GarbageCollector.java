package de.greencity.bladenightapp.procession;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GarbageCollector {
	public GarbageCollector(Collection<Participant> participants) {
		init(participants, 4.0);
	}

	public GarbageCollector(Collection<Participant> participants, double factor) {
		init(participants,factor);
	}
	
	private void init(Collection<Participant> participants, double factor) {
		this.participants = participants;
		this.referenceTime = System.currentTimeMillis();
		this.factor = factor;
	}
	
	public void setReferenceTime(long time) {
		referenceTime = time;
	}

	public Collection<Participant> collect() {
		long meanAge = getMeanUpdateAge();
		if ( meanAge <=0 )
			return participants;
		for(Participant p: participants) {
			long age = getAge(p);
			if ( age >= factor * meanAge ) {
				getLog().info("Removing participant " + p.getDeviceId() + " : " + age + " >= " + meanAge);
				participants.remove(p);
			}
		}
		return participants;
	}

	private long getMeanUpdateAge() {
		long sum=0;
		long count=0;
		for (Participant p : participants) {
			count++;
			sum += getAge(p); 
		}
		return (long)(sum / count); 
	}

	private long getAge(Participant p) {
		long diff = referenceTime - p.getLastLifeSign();
		if ( diff < 0 )
			return 0;
		return diff;
	}


	private static Log log;
	
	public static void setLog(Log log) {
		GarbageCollector.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(GarbageCollector.class));
		return log;
	}
	

	private Collection<Participant> participants;
	private long referenceTime;
	private double factor;
}
