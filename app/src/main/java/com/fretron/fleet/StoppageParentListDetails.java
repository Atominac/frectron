package com.fretron.fleet;

import java.util.ArrayList;

public class StoppageParentListDetails {
    private String title, speed, distance;
    public ArrayList<StoppageChildListDetails> childListDetailses;

    public  StoppageParentListDetails() {
    }

    public StoppageParentListDetails(String title, String speed, String distance , ArrayList<StoppageChildListDetails> childListDetailses) {
        this.title = title;
        this.speed = speed;
        this.distance = distance;
        this.childListDetailses = childListDetailses;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
