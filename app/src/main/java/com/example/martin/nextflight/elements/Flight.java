package com.example.martin.nextflight.elements;

/**
 * Created by Martin on 18/11/2016.
 */

public class Flight {

    private String number;
    private Airline airline;
    private String status;
    private Departure departure;
    private Arrival arrival;

    public Flight(String number, Airline airline, String status, Arrival arrival, Departure departure) {
        this.number = number;
        this.airline = airline;
        this.status = status;
        this.arrival = arrival;
        this.departure = departure;
    }

    public String getFlight_number() {
        return number;
    }

    public void setFlight_number(String number) {
        this.number = number;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
