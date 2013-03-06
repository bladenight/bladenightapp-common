package de.greencity.bladenightapp.relationships;


public class RelationshipStoreSingleton {

	public static RelationshipStore getInstance() {
		return instance;
	}

	public static void setInstance(RelationshipStore instance) {
		RelationshipStoreSingleton.instance = instance;
	}

	private static RelationshipStore instance;

}
