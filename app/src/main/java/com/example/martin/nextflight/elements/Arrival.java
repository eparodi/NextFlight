package com.example.martin.nextflight.elements;

import java.io.Serializable;

/**
 * Created by Martin on 18/11/2016.
 */

public class Arrival implements Serializable{

    private Airport airport;

    private String scheduled_time;
    private String actual_time;
    private String scheduled_gate_time;
    private String actual_gate_time;
    private String gate_delay;
    private String estimate_runway_time;
    private String actual_runway_time;
    private String runway_delay;

    public Arrival(String gate_delay, Airport airport, String scheduled_time, String actual_time, String scheduled_gate_time, String actual_gate_time, String estimate_runway_time, String actual_runway_time, String runway_delay) {
        this.gate_delay = gate_delay;
        this.airport = airport;
        this.scheduled_time = scheduled_time;
        this.actual_time = actual_time;
        this.scheduled_gate_time = scheduled_gate_time;
        this.actual_gate_time = actual_gate_time;
        this.estimate_runway_time = estimate_runway_time;
        this.actual_runway_time = actual_runway_time;
        this.runway_delay = runway_delay;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public String getScheduled_time() {
        return scheduled_time;
    }

    public void setScheduled_time(String scheduled_time) {
        this.scheduled_time = scheduled_time;
    }

    public String getActual_time() {
        return actual_time;
    }

    public void setActual_time(String actual_time) {
        this.actual_time = actual_time;
    }

    public String getScheduled_gate_time() {
        return scheduled_gate_time;
    }

    public void setScheduled_gate_time(String scheduled_gate_time) {
        this.scheduled_gate_time = scheduled_gate_time;
    }

    public String getActual_gate_time() {
        return actual_gate_time;
    }

    public void setActual_gate_time(String actual_gate_time) {
        this.actual_gate_time = actual_gate_time;
    }

    public String getGate_delay() {
        return gate_delay;
    }

    public void setGate_delay(String gate_delay) {
        this.gate_delay = gate_delay;
    }

    public String getEstimate_runway_time() {
        return estimate_runway_time;
    }

    public void setEstimate_runway_time(String estimate_runway_time) {
        this.estimate_runway_time = estimate_runway_time;
    }

    public String getActual_runway_time() {
        return actual_runway_time;
    }

    public void setActual_runway_time(String actual_runway_time) {
        this.actual_runway_time = actual_runway_time;
    }

    public String getRunway_delay() {
        return runway_delay;
    }

    public void setRunway_delay(String runway_delay) {
        this.runway_delay = runway_delay;
    }

}
