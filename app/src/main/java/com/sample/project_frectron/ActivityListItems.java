package com.sample.project_frectron;

public class ActivityListItems {
    private String title, speed, starting_date,location,status,starting_time,vtsDeviceId;

    public  ActivityListItems() {
    }

        public ActivityListItems(String title, String speed,
                                 String starting_date,String location,
                                 String status,String starting_time , String vtsDeviceId ) {
            this.title = title;
            this.speed = speed;
            this.starting_date = starting_date;
            this.location = location;
            this.status = status;
            this.starting_time = starting_time;
            this.vtsDeviceId = vtsDeviceId ;
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

        public String getStarting_date() {
        return starting_date;
        }

        public void setStarting_date(String starting_date) {
        this.starting_date = starting_date;
        }

        public String getLocation() {
        return location;
    }

        public void setLocation(String location) {
        this.location = location;
    }

        public String getStatus() {
        return status;
    }

        public void setStatus(String status) {
        this.status = status;
    }

        public String getStarting_time() {
        return starting_time;
    }

        public void setStarting_time(String starting_time) {
        this.starting_time = starting_time;
    }


        public String getVtsDeviceId() {
        return vtsDeviceId;
    }

        public void setVtsDeviceId(String vtsDeviceId) {
        this.vtsDeviceId = vtsDeviceId;
    }
}

