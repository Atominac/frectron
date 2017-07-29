package com.fretron.fleet.StoppedReport;

import com.fretron.fleet.StoppedReport.StoppageChildListDetails;

import java.util.ArrayList;

class StoppageParentListDetails {
    private String title, time;
    ArrayList<StoppageChildListDetails> childListDetailses;

    StoppageParentListDetails() {
    }

    public StoppageParentListDetails(String title, String time , ArrayList<StoppageChildListDetails> childListDetailses) {
        this.title = title;
        this.time = time;
        this.childListDetailses = childListDetailses;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
