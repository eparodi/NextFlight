package com.example.martin.nextflight.elements;

import java.io.Serializable;

public class Airline implements Serializable {

    private String id;
    private String name;

    public Airline(String airline_id, String airline_name, String logo) {
        this.id = airline_id;
        this.name = airline_name;
        this.logo = logo;
    }

    public Airline(String airline_id) {
        this.id = airline_id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    private String logo;

    public String getAirlineId() {
        return id;
    }

    public void setAirlineId(String airline_id) {
        this.id = airline_id;
    }

    public String getAirlineName() {
        return name;
    }

    public void setAirlineName(String airline_name) {
        this.name = airline_name;
    }

    public boolean equals(Object o){
        if (o == null || o.getClass() != Airline.class){
            return false;
        }else{
            Airline airline = (Airline) o;
            return airline.getAirlineId().equals(this.getAirlineId());
        }
    }
}
