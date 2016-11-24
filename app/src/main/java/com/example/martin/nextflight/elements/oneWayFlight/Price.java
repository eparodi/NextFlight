package com.example.martin.nextflight.elements.oneWayFlight;

/**
 * Created by Martin on 23/11/2016.
 */

public class Price {

    private Adults adults;
    private Total total;

    public Price(Adults adults, Total total) {
        this.adults = adults;
        this.total = total;
    }

    public Adults getAdults() {
        return adults;
    }

    public void setAdults(Adults adults) {
        this.adults = adults;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }
}
