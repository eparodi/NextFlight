package com.example.martin.nextflight;

/**
 * Created by Martin on 17/11/2016.
 */

public class Flight {

    private String state;
    private String airline_name;
    private String airline_id;
    private int number;
    private String from_name;
    private String from_id;
    private String to_name;
    private String to_id;
    private String arrival_time;
    private String arrival_date;
    private String arrival_terminal;
    private String arrival_door;
    private String departure_time;
    private String departure_date;
    private String departure_terminal;
    private String departure_door;
    private boolean followed;

    public Flight(String state, String airline_name, String airline_id, String from_name, int number, String from_id, String to_name, String to_id, String arrival_time, String arrival_date, String arrival_terminal, String arrival_door, String departure_time, String departure_date, String departure_terminal, String departure_door) {
        this.state = state;
        this.airline_name = airline_name;
        this.airline_id = airline_id;
        this.from_name = from_name;
        this.number = number;
        this.from_id = from_id;
        this.to_name = to_name;
        this.to_id = to_id;
        this.arrival_time = arrival_time;
        this.arrival_date = arrival_date;
        this.arrival_terminal = arrival_terminal;
        this.arrival_door = arrival_door;
        this.departure_time = departure_time;
        this.departure_date = departure_date;
        this.departure_terminal = departure_terminal;
        this.departure_door = departure_door;
    }

    public Flight(String state, String airline_id, int number) {
        this.state = state;
        this.airline_id = airline_id;
        this.number = number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAirline_name() {
        return airline_name;
    }

    public void setAirline_name(String airline_name) {
        this.airline_name = airline_name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getAirline_id() {
        return airline_id;
    }

    public void setAirline_id(String airline_id) {
        this.airline_id = airline_id;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(String arrival_date) {
        this.arrival_date = arrival_date;
    }

    public String getArrival_terminal() {
        return arrival_terminal;
    }

    public void setArrival_terminal(String arrival_terminal) {
        this.arrival_terminal = arrival_terminal;
    }

    public String getArrival_door() {
        return arrival_door;
    }

    public void setArrival_door(String arrival_door) {
        this.arrival_door = arrival_door;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public String getDeparture_date() {
        return departure_date;
    }

    public void setDeparture_date(String departure_date) {
        this.departure_date = departure_date;
    }

    public String getDeparture_terminal() {
        return departure_terminal;
    }

    public void setDeparture_terminal(String departure_terminal) {
        this.departure_terminal = departure_terminal;
    }

    public String getDeparture_door() {
        return departure_door;
    }

    public void setDeparture_door(String departure_door) {
        this.departure_door = departure_door;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public boolean equals(Object o) {
        if (o == null || !(o.getClass().equals(getClass())))
            return false;
        if (this == o) {
            return true;
        }
        Flight other = (Flight)o;
        if (this.number == other.number && this.airline_id.equals(other.airline_id))
            return true;
        return false;
    }
}
