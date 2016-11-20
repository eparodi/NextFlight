package com.example.martin.nextflight.elements;

public class Airline {

    private String id;
    private String name;

    public Airline(String airline_id, String airline_name, String logo) {
        this.id = airline_id;
        this.name = airline_name;
        this.logo = logo;
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

}
