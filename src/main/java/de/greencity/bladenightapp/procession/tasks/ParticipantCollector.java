package de.greencity.bladenightapp.procession.tasks;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.math.MedianFinder;
import de.greencity.bladenightapp.procession.Participant;
import de.greencity.bladenightapp.time.Clock;
import de.greencity.bladenightapp.time.SystemClock;


public class ParticipantCollector implements Runnable {
	public ParticipantCollector(ParticipantCollectorClient procession) {
		this.procession = procession;
	}

	public void setMaxRelativeAgeFactor(double maxRelativeAgeFactor) {
		this.maxRelativeAgeFactor = maxRelativeAgeFactor;
	}

	public void setMaxAbsoluteAge(long maxAbsoluteAge) {
		this.maxAbsoluteAge = maxAbsoluteAge;
	}

	public boolean hasMaxRelativeAge() {
		return maxRelativeAgeFactor > 0.0;
	}

	public boolean hasMaxAbsoluteAge() {
		return maxAbsoluteAge > 0.0;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public boolean hasPeriod() {
		return period > 0;
	}
	
	public void setClock(Clock clock) {
		this.clock = clock;
	}


	@Override
	public void run() {
		boolean cont = true;
		if ( ! hasPeriod() )
			throw new IllegalStateException("Period has not been set");
		
		while (cont) {
			collect();
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				cont = false;
			}
		}
	}
	
	public void collect() {
		long meanAge = 0;
		
		long referenceTime = clock.currentTimeMillis();
		
		if ( hasMaxRelativeAge() ) {
			meanAge = getMeanParticipantUpdatePeriod();
			getLog().debug("meanAge=" + meanAge);
		}

		for(Participant p: procession.getParticipants()) {
			long age = getAge(p, referenceTime);
			if ( hasMaxRelativeAge() && age > maxRelativeAgeFactor * meanAge ) {
				getLog().info("Removing participant " + p.getDeviceId() + " : " + age + " > " + maxRelativeAgeFactor + " * " + meanAge);
				procession.removeParticipant(p.getDeviceId());
			}
			if ( hasMaxAbsoluteAge() && age > maxAbsoluteAge ) {
				getLog().info("Removing participant " + p.getDeviceId() + " : " + age + " > " + maxAbsoluteAge);
				procession.removeParticipant(p.getDeviceId());
			}
		}
	}

	private long getAge(Participant p, long referenceTime) {
		long diff = referenceTime - p.getLastLifeSign();
		if ( diff < 0 )
			return 0;
		return diff;
	}

	private long getMeanParticipantUpdatePeriod() {
		Map<Long, Long> counter = new HashMap<Long, Long>();
		for (Participant p : procession.getParticipants() ) {
			Long age = clock.currentTimeMillis() - p.getLastLifeSign();
			Long currentCount = counter.get(age);
			if ( currentCount == null ) {
				currentCount = new Long(0);
			}
			counter.put(age, currentCount+1);
		}
		MedianFinder medianFinder = new MedianFinder();
		for (Long age : counter.keySet()) {
			medianFinder.addWeightedValue(age, counter.get(age));
		}
		if ( medianFinder.sampleCount() == 0 )
			return 0;
		return (long) medianFinder.findMedian();
	}
	

	private static Log log;
	
	public static void setLog(Log log) {
		ParticipantCollector.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(ParticipantCollector.class));
		return log;
	}
	

	private double maxRelativeAgeFactor;
	private long maxAbsoluteAge;
	private ParticipantCollectorClient procession;
	private long period;
	private Clock clock = new SystemClock();

}
