package de.greencity.bladenightapp.procession;

import de.greencity.bladenightapp.procession.Procession;

public class ProcessionSingleton {

	public static Procession getInstance() {
		return instance;
	}

	public static void setProcession(Procession instance) {
		ProcessionSingleton.instance = instance;
	}

	private static Procession instance;

}
