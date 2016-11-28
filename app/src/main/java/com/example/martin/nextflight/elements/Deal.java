package com.example.martin.nextflight.elements;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Deal implements Serializable{

    private City city;
    private double price;
    private Bitmap image = null;
    public boolean loadImage = false;

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

    public void setImage(Bitmap image) { this.image = image; }

    public Bitmap getImage() { return image; }

    public String toString(){
        return "A " + city.getName() + "\n Precio " + price;
    }

    public boolean equals(Object o){
        if (o == null || o.getClass() != Airline.class){
            return false;
        }else{
            Deal deal = (Deal) o;
            return this.city.equals(deal.city) && this.price == deal.getPrice();
        }
    }

    public int hashCode(){
        return city.getName().hashCode() * (Double.valueOf(price)).hashCode();
    }
}
