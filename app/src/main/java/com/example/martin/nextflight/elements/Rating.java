package com.example.martin.nextflight.elements;

/**
 * Created by Martin on 18/11/2016.
 */

public class Rating {

    private String overall;
    private String friendliness;
    private String food;
    private String punctuality;
    private String mileage_program;
    private String comfort;
    private String quality_price;

    public Rating(String overall, String friendliness, String food, String punctuality, String mileage_program, String comfort, String quality_price) {
        this.overall = overall;
        this.friendliness = friendliness;
        this.food = food;
        this.punctuality = punctuality;
        this.mileage_program = mileage_program;
        this.comfort = comfort;
        this.quality_price = quality_price;
    }

    public String getOverall() {
        return overall;
    }

    public void setOverall(String overall) {
        this.overall = overall;
    }

    public String getFriendliness() {
        return friendliness;
    }

    public void setFriendliness(String friendliness) {
        this.friendliness = friendliness;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getPunctuality() {
        return punctuality;
    }

    public void setPunctuality(String punctuality) {
        this.punctuality = punctuality;
    }

    public String getMileage_program() {
        return mileage_program;
    }

    public void setMileage_program(String mileage_program) {
        this.mileage_program = mileage_program;
    }

    public String getComfort() {
        return comfort;
    }

    public void setComfort(String comfort) {
        this.comfort = comfort;
    }

    public String getQuality_price() {
        return quality_price;
    }

    public void setQuality_price(String quality_price) {
        this.quality_price = quality_price;
    }
}
