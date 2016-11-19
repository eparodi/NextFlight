package com.example.martin.nextflight.elements;

import java.io.Serializable;

/**
 * Created by Martin on 18/11/2016.
 */

public class Review implements Serializable {

    private Flight flight;
    private Rating rating;
    private String yes_recommend;
    private String comments;

    public Review(Flight flight, Rating rating, String yes_recommend, String comments) {

        this.flight = flight;
        this.rating = rating;
        this.yes_recommend = yes_recommend;
        this.comments = comments;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) { this.rating = rating; }

    public String getYes_recommend() {
        return yes_recommend;
    }

    public void setYes_recommend(String yes_recommend) {
        this.yes_recommend = yes_recommend;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
