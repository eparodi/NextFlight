package com.example.martin.nextflight.elements;

/**
 * Created by Martin on 18/11/2016.
 */

public class Airport {

    private String id;
    private String description;
    private City city;
    private String terminal;
    private String gate;

    public Airport(String id, City city, String description, String terminal, String gate) {
        this.id = id;
        this.city = city;
        this.description = description;
        this.terminal = terminal;
        this.gate = gate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }
}
