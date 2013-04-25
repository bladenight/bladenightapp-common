package de.greencity.bladenightapp.relationships.tasks;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.greencity.bladenightapp.relationships.RelationshipStore;
import de.greencity.bladenightapp.time.Sleep;

public class RelationshipCollector implements Runnable {
	public RelationshipCollector(RelationshipStore store, long period, long maxAge) {
		this.store = store;
		this.period = period;
		this.maxAge = maxAge;
	}
	
	@Override
	public void run() {
		boolean cont = true;
		while (cont) {
			int hits = store.removePendingRelationshipsOlderThan(maxAge);
			if ( hits > 0 )
				try {
					store.write();
				} catch (IOException e) {
					getLog().error("Error while writting:",e);
				}
			try {
				Sleep.sleep(period);
			} catch (InterruptedException e) {
				cont = false;
			}
		}
	}
	
	public boolean shallContinue() {
		return true;
	}

	private RelationshipStore store;
	private long period;
	private long maxAge;

	private static Log log;

	public static void setLog(Log log) {
		RelationshipCollector.log = log;
	}

	protected static Log getLog() {
		if (log == null)
			setLog(LogFactory.getLog(RelationshipCollector.class));
		return log;
	}
}
