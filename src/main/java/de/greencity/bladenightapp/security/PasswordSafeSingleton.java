package de.greencity.bladenightapp.security;


public class PasswordSafeSingleton {

	public static PasswordSafe getInstance() {
		return instance;
	}

	public static void setInstance(PasswordSafe instance) {
		PasswordSafeSingleton.instance = instance;
	}

	private static PasswordSafe instance;

}
