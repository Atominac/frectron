package com.fretron.fleet.StoppedReport;

class StoppageChildListDetails {
    private String title, speed , location ;
    private Double latitude , longitude ;


    StoppageChildListDetails(String title, String speed, String loca, Double lat, Double lng) {
        this.title = title;
        this.speed = speed;
        this.location = loca ;
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
