package com.example.project;

public class Favorite {
    public int id;
    public String userEmail;
    public int propertyId;
    public long createdAt;

    public Property property;
    public Favorite() {}

    public Favorite(Property property) {
        this.property = property;
    }
}
