package com.example.martin.nextflight.elements.flickr;

import java.util.ArrayList;

/**
 * Created by Martin on 23/11/2016.
 */

public class FlickrObject {
    private Integer page;
    private ArrayList<Photo> photo;

    public FlickrObject(Integer page, ArrayList<Photo> photo) {
        this.page = page;
        this.photo = photo;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public ArrayList<Photo> getPhoto() {
        return photo;
    }

    public void setPhoto(ArrayList<Photo> photo) {
        this.photo = photo;
    }
}
