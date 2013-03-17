package de.greencity.bladenightapp.network.scanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.greencity.bladenightapp.time.Sleep;

/***
 * Scans the given hosts and subnets for the given open port. 
 * To speed up the scan of subnets, it starts up to  MAX_RUNNING_JOBS threads in parallel
 * to try knock on the ports.
 */
public class PortScanner {

	public PortScanner(int port) {
		this.port = port;
		hostsToScan = new ArrayList<String>();
		runningPortKnockersJobs = Collections.synchronizedSet(new LinkedHashSet<PortKnockerJob>());
		hostsFound = new ArrayList<String>();
	}

	public void addHost(String host) {
		hostsToScan.add(host);
	}

	/**
	 * Add all the hosts of the given network to the list of hosts to scan.
	 * It assumes a /24 network.
	 * @param subnet "192.168.0"
	 */
	public void addSubnet(String subnet) {
		for (int i=1; i<=254; i++)
			addHost(subnet+"."+i);
	}

	/**
	 * Scan the hosts defined with addhost() and addSubnet().
	 * When it returns, you can all getFoundHost()
	 * @throws InterruptedException
	 */
	public void scan() throws InterruptedException {
		for ( String host : hostsToScan ) {
			if ( hostsFound.size() > 0 ) {
				break;
			}
			waitIfTooManyKnockersRunning(MAX_RUNNING_JOBS-1);
			PortKnockerJob job = new PortKnockerJob(factory.createKnocker(), host, port, timeout);
			runningPortKnockersJobs.add(job); // the job will remove itself from the list
			new Thread(job).start();
		}
		if ( hostsFound.size() == 0 )
			waitIfTooManyKnockersRunning(0);

		return;
	}

	public void waitIfTooManyKnockersRunning(int maxRunning) throws InterruptedException {
		while ( runningPortKnockersJobs.size() > maxRunning ) {
			long sleepInterval = Math.min(10, timeout / 2);
			Sleep.sleep(sleepInterval);
		}
	}

	public String getFoundHost() {
		if ( hostsFound.size() > 0 )
			return hostsFound.get(0);
		else
			return null;
	}

	public void setKnockerFactory(PortKnockerFactory factory) {
		this.factory = factory;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public interface PortKnockerFactory {
		public PortKnocker createKnocker();
	}

	public class DefaultKnockerFactory implements PortKnockerFactory {
		public PortKnocker createKnocker() {
			return new PortKnocker();
		}
	}

	private class PortKnockerJob implements Runnable {
		protected PortKnocker knocker;
		PortKnockerJob(PortKnocker knocker, String host, int port, int timeout ) {
			this.knocker = knocker;
			knocker.setHost(host); 
			knocker.setPort(port); 
			knocker.setTimeout(timeout); 
		}
		@Override
		public void run() {
			if ( knocker.knock() ) {
				hostsFound.add(knocker.getHost());
			}
			runningPortKnockersJobs.remove(this);
		}
	}

	private static final int MAX_RUNNING_JOBS = 20;

	protected int timeout = 10000;
	protected int port;
	protected List<String> hostsToScan;
	private final List<String> hostsFound;
	private final Set<PortKnockerJob> runningPortKnockersJobs;
	protected PortKnockerFactory factory = new DefaultKnockerFactory();
}
