package com.example.martin.nextflight.elements.flickr;

/**
 * Created by Martin on 23/11/2016.
 */

public class Photo {
    private String id;
    private String owner;
    private String secret;
    private String server;
    private String farm;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

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
