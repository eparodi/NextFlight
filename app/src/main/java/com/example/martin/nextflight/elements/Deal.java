package com.example.martin.nextflight.elements;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class Deal implements Serializable{

    private City city;
    private double price;
    private transient Bitmap image = null;
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

    /*
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
        byte bitmapBytes[] = byteStream.toByteArray();
        out.write(bitmapBytes, 0, bitmapBytes.length);
    }
    // Deserializes a byte array representing the Bitmap and decodes it
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int b;
        while((b = in.read()) != -1)
            byteStream.write(b);
        byte bitmapBytes[] = byteStream.toByteArray();
        image = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
    }*/
}
