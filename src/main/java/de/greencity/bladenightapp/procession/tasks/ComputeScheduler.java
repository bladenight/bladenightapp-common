package de.greencity.bladenightapp.procession.tasks;


public class ComputeScheduler implements Runnable {
	public ComputeScheduler(ComputeSchedulerClient procession, long period) {
		this.procession = procession;
		this.period = period;
	}
	
	@Override
	public void run() {
		boolean cont = true;
		while (cont) {
			procession.compute();
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				cont = false;
			}
		}
	}
	
	public boolean shallContinue() {
		return true;
	}
	
	private ComputeSchedulerClient procession;
	private long period;
}
