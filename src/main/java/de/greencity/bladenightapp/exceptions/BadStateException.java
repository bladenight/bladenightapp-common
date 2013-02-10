package de.greencity.bladenightapp.exceptions;

public class BadStateException extends Exception {
	private static final long serialVersionUID = -7689513708768691734L;
	public BadStateException() {
		super();
	}
	public BadStateException(String message) {
		super(message); 
	}
	public BadStateException(String message, Throwable cause) { 
		super(message, cause); 
	}
	public BadStateException(Throwable cause) {
		super(cause); 
	}
}
