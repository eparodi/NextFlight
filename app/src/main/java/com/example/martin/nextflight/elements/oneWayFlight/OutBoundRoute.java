package com.example.martin.nextflight.elements.oneWayFlight;

import java.util.ArrayList;

/**
 * Created by Martin on 23/11/2016.
 */

public class OutBoundRoute {

    ArrayList<Segment> segments;

    public OutBoundRoute(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }
}
