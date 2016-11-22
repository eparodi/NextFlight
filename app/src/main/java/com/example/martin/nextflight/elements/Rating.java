package com.example.martin.nextflight.elements;

import java.io.Serializable;

/**
 * Created by Martin on 18/11/2016.
 */

public class Rating implements Serializable {

    private Double overall;
    private Integer friendliness;
    private Integer food;
    private Integer punctuality;
    private Integer mileage_program;
    private Integer comfort;
    private Integer quality_price;

    public Rating(Double overall, Integer friendliness, Integer food, Integer punctuality, Integer mileage_program, Integer comfort, Integer quality_price) {
        this.overall = overall;
        this.friendliness = friendliness;
        this.food = food;
        this.punctuality = punctuality;
        this.mileage_program = mileage_program;
        this.comfort = comfort;
        this.quality_price = quality_price;
    }

    public Double getOverall() {
        return overall;
    }

    public void setOverall(Double overall) {
        this.overall = overall;
    }

    public Integer getFriendliness() {
        return friendliness;
    }

    public void setFriendliness(Integer friendliness) {
        this.friendliness = friendliness;
    }

    public Integer getFood() {
        return food;
    }

    public void setFood(Integer food) {
        this.food = food;
    }

    public Integer getPunctuality() {
        return punctuality;
    }

    public void setPunctuality(Integer punctuality) {
        this.punctuality = punctuality;
    }

    public Integer getMileage_program() {
        return mileage_program;
    }

    public void setMileage_program(Integer mileage_program) {
        this.mileage_program = mileage_program;
    }

    public Integer getComfort() {
        return comfort;
    }

    public void setComfort(Integer comfort) {
        this.comfort = comfort;
    }

    public Integer getQuality_price() {
        return quality_price;
    }

    public void setQuality_price(Integer quality_price) {
        this.quality_price = quality_price;
    }
}
