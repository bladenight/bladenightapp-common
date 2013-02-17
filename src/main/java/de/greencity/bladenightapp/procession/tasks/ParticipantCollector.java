package de.greencity.bladenightapp.procession.tasks;


public class ParticipantCollector implements Runnable {
	public ParticipantCollector(ParticipantCollectorClient procession, double factor, long period) {
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
	
	private ParticipantCollectorClient procession;
	private double factor;
	private long period;

}
