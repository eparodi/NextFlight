package com.example.martin.nextflight.elements;

/**
 * Created by Martin on 18/11/2016.
 */

public class City{

    private String id;
    private String name;
    private Country country;

    public City(String id, String name, Country country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Country getCountry() {
        return country;
    }

}
