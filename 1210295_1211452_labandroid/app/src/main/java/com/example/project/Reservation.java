package com.example.project;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class Reservation {
    public int id;
    public String userEmail;
    public int propertyId;

    public long reservationDate;
    public long createdAt;
    public String status;
    public int imageResourceId;

    public Property property;

    public Reservation() {
        this.status = "active";
        this.createdAt = System.currentTimeMillis() / 1000;
        this.reservationDate = System.currentTimeMillis() / 1000;
        this.imageResourceId = 0;
    }


    public String getFormattedReservationDate() {
        try {
            Date date = new Date(reservationDate * 1000);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
            return formatter.format(date);
        } catch (Exception e) {
            return "Unknown date";
        }
    }
    public String getFormattedCreatedAt() {
        try {
            Date date = new Date(createdAt * 1000);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
            return formatter.format(date);
        } catch (Exception e) {
            return "Unknown creation time";
        }
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", propertyId=" + propertyId +
                ", property=" + (property != null ? property.title : "null") +
                ", reservationDate=" + getFormattedReservationDate() +
                ", status='" + status + '\'' +
                ", imageResourceId=" + imageResourceId +
                '}';
    }
}