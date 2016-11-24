package com.example.martin.nextflight.elements.oneWayFlight;

import java.util.ArrayList;

/**
 * Created by Martin on 23/11/2016.
 */

public class OneWayFlight {

    Price price;
    ArrayList<OutBoundRoute> outbound_routes;

    public OneWayFlight(Price price, ArrayList<OutBoundRoute> outbound_routes) {
        this.price = price;
        this.outbound_routes = outbound_routes;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public ArrayList<OutBoundRoute> getOutbound_routes() {
        return outbound_routes;
    }

    public void setOutbound_routes(ArrayList<OutBoundRoute> outbound_routes) {
        this.outbound_routes = outbound_routes;
    }
}
