package de.greencity.bladenightapp.network.messages;

import java.util.List;

public class RouteNamesMessage {

    static public RouteNamesMessage newFromRouteNameList(List<String> routeNameList) {
        RouteNamesMessage message = new RouteNamesMessage();
        message.rna = routeNameList.toArray(new String[0]);
        return message;
    }

    public String[] rna;
}
