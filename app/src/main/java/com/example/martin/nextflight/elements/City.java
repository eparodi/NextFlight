package com.example.martin.nextflight.elements;

import java.io.Serializable;

/**
 * Created by Martin on 18/11/2016.
 */

public class City implements Serializable{

    private String id;
    private String name;
    private Country country;
    private double longitude;
    private double latitude;

    public City(String id, String name, Country country, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean equals(Object o){
        if (o == null || o.getClass() != Airline.class){
            return false;
        }else{
            City city = (City) o;
            return this.id.equals(city.getId());
        }
    }
}
