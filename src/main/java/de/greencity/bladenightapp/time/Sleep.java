package de.greencity.bladenightapp.time;

public class Sleep {
	/*** Sleeps for at least the time given (ms).
	 * This is just a wrapper around Thread.sleep(), that undersleeps for small 
	 * times on some platforms (Windows) 
	 * @param requestedMs time in milliseconds
	 * @throws InterruptedException
	 */
	static public void sleep(long requestedMs) throws InterruptedException {
		long start = System.currentTimeMillis();
		boolean goOn = true;
		while ( goOn ) {
			long current = System.currentTimeMillis() - start;
			if (  current < requestedMs )
				Thread.sleep(requestedMs-current);
			else
				goOn = false;
		}
	}
}
