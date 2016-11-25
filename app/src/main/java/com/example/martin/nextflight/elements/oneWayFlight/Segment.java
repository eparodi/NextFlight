package com.example.martin.nextflight.elements.oneWayFlight;

import com.example.martin.nextflight.elements.Airline;
import com.example.martin.nextflight.elements.Arrival;
import com.example.martin.nextflight.elements.Departure;

public class Segment {

    private Integer number;
    private Airline airline;
    private Arrival arrival;
    private Departure departure;

    public Segment(Integer number, Airline airline, Arrival arrival, Departure departure) {
        this.number = number;
        this.airline = airline;
        this.arrival = arrival;
        this.departure = departure;
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

    public Departure getDeparture() {
        return departure;
    }

    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    public Arrival getArrival() {
        return arrival;
    }

    public void setArrival(Arrival arrival) {
        this.arrival = arrival;
    }
}
