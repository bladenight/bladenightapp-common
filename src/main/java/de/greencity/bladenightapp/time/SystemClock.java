package de.greencity.bladenightapp.time;

public class SystemClock implements Clock {

	@Override
	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}

}
