package de.greencity.bladenightapp.time;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ControlledClock implements Clock {

	public ControlledClock() {
		time = System.currentTimeMillis();
	}

	public ControlledClock(long time) {
		this.time = time;
	}
	
	@Override
	public long currentTimeMillis() {
		return time;
	}
	
	public void increment(long delta) {
		time += delta;
	}

	public void set(long time) {
		this.time = time;
	}

	private long time;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
