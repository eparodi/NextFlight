package com.example.martin.nextflight.elements;

import java.io.Serializable;

/**
 * Created by Martin on 18/11/2016.
 */

public class Country implements Serializable{

    private String id;
    private String name;

    public Country(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return String.format("%s - %s", this.getId(), this.getName());
    }

}
