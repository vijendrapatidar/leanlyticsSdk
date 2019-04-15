package com.eventtrack.leanlyticssdk;

import java.util.ArrayList;

public class EventDetail {
    private String controlName;
    private String id;
    private ArrayList<Event> event;

    public EventDetail(String controlName, String id, ArrayList<Event> event) {
        this.controlName = controlName;
        this.id = id;
        this.event = event;
    }

    public String getControlName() {
        return controlName;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Event> getEvent() {
        return event;
    }
}
