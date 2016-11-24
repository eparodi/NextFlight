package com.example.martin.nextflight.elements.oneWayFlight;

/**
 * Created by Martin on 23/11/2016.
 */

public class Adults {

    private Integer base_fare;
    private Integer quantity;

    public Adults(Integer base_fare, Integer quantity) {
        this.base_fare = base_fare;
        this.quantity = quantity;
    }

    public Integer getBase_fare() {
        return base_fare;
    }

    public void setBase_fare(Integer base_fare) {
        this.base_fare = base_fare;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}