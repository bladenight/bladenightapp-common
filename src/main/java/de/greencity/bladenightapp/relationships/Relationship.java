package de.greencity.bladenightapp.relationships;

import org.apache.commons.lang3.builder.ToStringBuilder;

class Relationship {

	public Relationship(String deviceId1) {
		this.deviceId1 = deviceId1;
		this.creationTimestamp = System.currentTimeMillis();
	}

	public String getDeviceId1() {
		return deviceId1;
	}

	public String getDeviceId2() {
		return deviceId2;
	}
	
	public void setDeviceId2(String deviceId2) {
		this.deviceId2 = deviceId2;
	}
	
	public boolean isPending() {
		return deviceId2 == null;
	}

	public long getAge() {
		return System.currentTimeMillis() - creationTimestamp;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private String deviceId1;
	private String deviceId2;
	private long creationTimestamp;
}
