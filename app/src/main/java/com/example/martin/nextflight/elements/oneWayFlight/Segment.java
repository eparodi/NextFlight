package com.example.martin.nextflight.elements.oneWayFlight;

import com.example.martin.nextflight.elements.Airline;

/**
 * Created by Martin on 23/11/2016.
 */

public class Segment {

    private Integer number;
    private Airline airline;

    public Segment(Integer number, Airline airline) {
        this.number = number;
        this.airline = airline;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }
}
