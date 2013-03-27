package de.greencity.bladenightapp.protocol;


public class ProtocolSingleton {
	

	public void setInstance(Protocol instance) {
		this.instance = instance;
	}

	public Protocol getInstance() {
		return this.instance;
	}
	
	public void write(String tag1, String tag2, String message) {
		instance.write(tag1, tag2, message);
	}

	private Protocol instance;
}
