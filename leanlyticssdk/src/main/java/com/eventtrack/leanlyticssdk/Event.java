package com.eventtrack.leanlyticssdk;

public class Event {
    private String label;
    private String id;
    private Double[] coordinates;

    public Event(String label, String id, Double[] coordinates) {
        this.label = label;
        this.id = id;
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Double[] getCoordinates() {
        return coordinates;
    }

}
