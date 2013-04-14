package de.greencity.bladenightapp.network.messages;


public class SetActiveRouteMessage extends AdminMessage {

	public SetActiveRouteMessage(String routeName, String password) {
		setRouteName(routeName);
		authenticate(password);
	}
	
	public String getRouteName() {
		return rou;
	}

	public void setRouteName(String rou) {
		this.rou = rou;
	}

	private String rou;
}
