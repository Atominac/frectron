package com.fretron.fleet;

import java.util.ArrayList;

public class StoppageParentListDetails {
    private String title, time;
    public ArrayList<StoppageChildListDetails> childListDetailses;

    public  StoppageParentListDetails() {
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
