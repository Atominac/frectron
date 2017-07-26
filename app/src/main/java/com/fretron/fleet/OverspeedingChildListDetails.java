package com.fretron.fleet;

public class OverspeedingChildListDetails {
    private String title, speed, distance,location;
    private Double latitude , longitude ;


    public OverspeedingChildListDetails(String title, String speed, String distance, String location ,Double lat , Double lng ) {
        this.title = title;
        this.speed = speed;
        this.distance = distance;
        this.location = location ;
        this.latitude = lat ;
        this.longitude = lng ;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
