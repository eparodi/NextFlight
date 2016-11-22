package com.example.martin.nextflight.elements;

/**
 * Created by Martin on 21/11/2016.
 */

public class SubmitReview {
    private Flight flight;
    private Rating rating;
    private Boolean yes_recommend;
    private String comments;

    public SubmitReview(Flight flight, Rating rating, Boolean yes_recommend, String comments) {
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

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Boolean getYes_recommend() {
        return yes_recommend;
    }

    public void setYes_recommend(Boolean yes_recommend) {
        this.yes_recommend = yes_recommend;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
