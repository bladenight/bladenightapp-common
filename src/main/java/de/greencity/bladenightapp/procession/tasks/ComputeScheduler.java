package de.greencity.bladenightapp.procession.tasks;

import de.greencity.bladenightapp.procession.Procession;

public class ComputeScheduler implements Runnable {
	public ComputeScheduler(Procession procession, long period) {
		this.procession = procession;
		this.period = period;
	}
	
	@Override
	public void run() {
		boolean cont = true;
		while (cont) {
			procession.computeProcession();
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				cont = false;
			}
		}
	}
	
	private Procession procession;
	private long period;
}
