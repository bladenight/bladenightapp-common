package de.greencity.bladenightapp.procession.tasks;

import de.greencity.bladenightapp.procession.Procession;

public class ParticipantCollector implements Runnable {
	public ParticipantCollector(Procession procession, double factor, long period) {
		this.procession = procession;
		this.factor = factor;
		this.period = period;
	}
	
	@Override
	public void run() {
		boolean cont = true;
		while (cont) {
			procession.removeOutdatedParticipants(factor);
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				cont = false;
			}
		}
	}
	
	private Procession procession;
	private double factor;
	private long period;

}
