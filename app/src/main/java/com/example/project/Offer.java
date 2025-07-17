package com.example.project;

public class Offer {
    private final int id;
    private final int propertyId;
    private final String title;
    private final String description;
    private final String startDate;
    private final String endDate;

    public Offer(int id, int propertyId, String title, String description, String startDate, String endDate) {
        this.id = id;
        this.propertyId = propertyId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() { return id; }
    public int getPropertyId() { return propertyId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
}
