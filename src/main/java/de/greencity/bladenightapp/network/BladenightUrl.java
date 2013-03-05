package de.greencity.bladenightapp.network;

public enum BladenightUrl {
	GET_ACTIVE_EVENT("http://www.greencity.de/bladenight/app/rpc/getActiveEvent"),
	GET_ACTIVE_ROUTE("http://www.greencity.de/bladenight/app/rpc/getActiveRoute"),
	GET_ROUTE("http://www.greencity.de/bladenight/app/rpc/getRoute"),
	GET_ALL_PARTICIPANTS("http://www.greencity.de/bladenight/app/rpc/getAllParticipants"),
	GET_REALTIME_UPDATE("http://www.greencity.de/bladenight/app/rpc/getRealtimeUpdate"),
	CREATE_RELATIONSHIP("http://www.greencity.de/bladenight/app/rpc/createRelationship")
    ;

	final public static String BASE = "http://www.greencity.de/bladenight/app/";
	
    private BladenightUrl(final String text) {
        this.text = text;
    }
    
    public String getText() {
    	return text;
    }

    private final String text;

    @Override
    public String toString() {
        return "BladenightUrl:"+text;
    }
}
