package de.greencity.bladenightapp.routes;

public class RouteStoreSingleton {

	public static RouteStore getInstance() {
		return instance;
	}

	public static void setInstance(RouteStore instance) {
		RouteStoreSingleton.instance = instance;
	}

	private static RouteStore instance;

}
