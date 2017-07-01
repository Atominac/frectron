package com.sample.project_frectron;

public class StoppageChildListDetails {
    private String title, speed, distance;


    public StoppageChildListDetails(String title, String speed, String distance) {
        this.title = title;
        this.speed = speed;
        this.distance = distance;
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
