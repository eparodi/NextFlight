package com.example.martin.nextflight.elements.flickr;

/**
 * Created by Martin on 23/11/2016.
 */

public class Photo {
    private String id;
    private String owner;

    public Photo(String id, String owner) {
        this.id = id;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
