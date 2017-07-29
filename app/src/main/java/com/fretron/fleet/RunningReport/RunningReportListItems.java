package com.fretron.fleet.RunningReport;

class RunningReportListItems {

    private String vehicle_name , initial_destination,
            total_time,
            total_distance,
            final_destination ,
            average_speed,
            stoppage_time,
            drive_time,
            overspeed_duration,
            disconnect_duration;

    public RunningReportListItems() {
    }

    RunningReportListItems(String vehicle_name, String initial_destination,
                           String total_time,
                           String total_distance,
                           String final_destination,
                           String average_speed,
                           String stoppage_time,
                           String drive_time,
                           String overspeed_duration,
                           String disconnect_duration) {
        this.vehicle_name = vehicle_name;
        this.initial_destination = initial_destination;
        this.total_time=total_time;
        this.total_distance=total_distance;
        this.final_destination = final_destination;
        this.average_speed = average_speed;
        this.stoppage_time = stoppage_time;
        this.drive_time = drive_time;
        this.overspeed_duration = overspeed_duration;
        this.disconnect_duration = disconnect_duration;
    }


    String getVehicle_name() {
        return vehicle_name;
    }
    public void setVehicle_name(String name) {
        this.vehicle_name = name;
    }

    String getInitial_destination() {
        return initial_destination;
    }
    public void setInitial_destination(String initial_destination) {
        this.initial_destination = initial_destination;
    }


    String getTotal_time() {
        return total_time;}
    public  void setTotal_time( String total_time){
        this.total_time = total_time;
    }

    String getTotal_distance() {
        return total_distance;
    }
    public void setTotal_distance(String total_distance) {
        this.total_distance = total_distance;
    }


    String getFinal_destination() {
        return final_destination;
    }
    public void setFinal_destination(String final_destination) {
        this.final_destination = final_destination;
    }


    public void setAverage_speed(String average_speed) {
        this.average_speed = average_speed;
    }
    String getAverage_speed() {
        return average_speed;
    }

    public void setDrive_time(String drive_time) {
        this.drive_time = drive_time;
    }
    String getDrive_time() {
        return drive_time;
    }

    public void setStoppage_time(String stoppage_time) {
        this.stoppage_time = stoppage_time;
    }
    String getStoppage_time() {
        return stoppage_time;
    }

    public void setOverspeed_duration(String overspeed_duration) {
        this.overspeed_duration = overspeed_duration;
    }
    String getOverspeed_duration() {
        return overspeed_duration;
    }

    public void setDisconnect_duration(String disconnect_duration) {
        this.disconnect_duration = disconnect_duration;
    }
    String getDisconnect_duration() {
        return disconnect_duration;
    }

}
