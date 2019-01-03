package de.greencity.bladenightapp.network;

public enum BladenightUrl {
    GET_ACTIVE_EVENT("http://www.greencity.de/bladenight/app/rpc/getActiveEvent"),
    GET_ACTIVE_ROUTE("http://www.greencity.de/bladenight/app/rpc/getActiveRoute"),
    GET_ROUTE("http://www.greencity.de/bladenight/app/rpc/getRoute"),
    GET_ALL_PARTICIPANTS("http://www.greencity.de/bladenight/app/rpc/getAllParticipants"),
    GET_REALTIME_UPDATE("http://www.greencity.de/bladenight/app/rpc/getRealtimeUpdate"),
    CREATE_RELATIONSHIP("http://www.greencity.de/bladenight/app/rpc/createRelationship"),
    GET_ALL_EVENTS("http://www.greencity.de/bladenight/app/rpc/getAllEvents"),
    SET_ACTIVE_ROUTE("http://www.greencity.de/bladenight/app/rpc/setActiveRoute"),
    SET_ACTIVE_STATUS("http://www.greencity.de/bladenight/app/rpc/setActiveStatus"),
    GET_ALL_ROUTE_NAMES("http://www.greencity.de/bladenight/app/rpc/getAllRouteNames"),
    VERIFY_ADMIN_PASSWORD("http://www.greencity.de/bladenight/app/rpc/verifyAdminPassword"),
    GET_FRIENDS("http://www.greencity.de/bladenight/app/rpc/getFriends"),
    DELETE_RELATIONSHIP("http://www.greencity.de/bladenight/app/rpc/deleteRelationship"),
    SET_MIN_POSITION("http://www.greencity.de/bladenight/app/rpc/setMinimumLinearPosition"),
    KILL_SERVER("http://www.greencity.de/bladenight/app/rpc/killServer"),
    SHAKE_HANDS("http://www.greencity.de/bladenight/app/rpc/shakeHand"),
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
