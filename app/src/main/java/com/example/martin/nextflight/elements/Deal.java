package com.example.martin.nextflight.elements;

import java.io.Serializable;

public class Deal implements Serializable{

    private City city;
    private double price;

    public Deal(City city, double price){
        this.city = city;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String toString(){
        return "A " + city.getName() + "\n Precio " + price;
    }
}
