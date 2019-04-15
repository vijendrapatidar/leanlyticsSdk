package com.eventtrack.leanlyticssdk;

public class SendEvents {
    private String label;
    private Double[] coordinates;

    public SendEvents(String label, Double[] coordinates) {
        this.label = label;
        this.coordinates = coordinates;
    }

    public String getLabel() {
        return label;
    }

    public Double[] getCoordinates() {
        return coordinates;
    }
}
