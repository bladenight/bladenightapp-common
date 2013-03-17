package de.greencity.bladenightapp.time;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemClock implements Clock {

	@Override
	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
