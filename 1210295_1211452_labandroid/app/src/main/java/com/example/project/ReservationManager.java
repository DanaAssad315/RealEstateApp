package com.example.project;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;

public class ReservationManager {
    private static final String TAG = "ReservationManager";
    private static DBHelper dbHelper;
    private static String currentUserEmail;
    private static ArrayList<Reservation> userReservations = new ArrayList<>();

    public static void initialize(Context context, String userEmail) {
        Log.d(TAG, "Initializing ReservationManager for user: " + userEmail);

        // Clean up previous instance if exists
        if (dbHelper != null) {
            clearInstance();
        }

        dbHelper = new DBHelper(context);
        currentUserEmail = userEmail;
        userReservations = new ArrayList<>();
        loadUserReservations();

        Log.d(TAG, "ReservationManager initialized successfully for user: " + userEmail);
    }

    public static boolean isInitialized() {
        boolean initialized = dbHelper != null && currentUserEmail != null && !currentUserEmail.trim().isEmpty();
        Log.d(TAG, "IsInitialized check: " + initialized);
        return initialized;
    }

    public static boolean addReservation(Property property) {
        return addReservation(property, System.currentTimeMillis() / 1000, 0);
    }

    public static boolean addReservation(Property property, long reservationDate, int imageResId) {
        Log.d(TAG, "Adding reservation for property: " + property.title + " (ID: " + property.id + ")");

        if (!isInitialized()) {
            Log.e(TAG, "ReservationManager not initialized!");
            return false;
        }

        if (property == null) {
            Log.e(TAG, "Property is null!");
            return false;
        }
        if (isReserved(property)) {
            Log.w(TAG, "Property already reserved by user");
            return false;
        }

        boolean success;
        try {

            success = dbHelper.addReservationn(currentUserEmail, property.id, reservationDate, imageResId);
        } catch (Exception e) {
            Log.e(TAG, "Exception while adding reservation", e);
            return false;
        }

        if (success) {
            Log.d(TAG, "Reservation added successfully to database");
            loadUserReservations();
        } else {
            Log.e(TAG, "Failed to add reservation to database");
        }

        return success;
    }
    public static boolean removeReservation(int reservationId) {
        if (!isInitialized()) {
            Log.e(TAG, "ReservationManager not initialized!");
            return false;
        }

        boolean success;
        try {
            success = dbHelper.removeReservation(reservationId, currentUserEmail);
        } catch (Exception e) {
            Log.e(TAG, "Exception while removing reservation", e);
            return false;
        }

        if (success) {
            Log.d(TAG, "Reservation removed successfully from database");
            loadUserReservations();
        } else {
            Log.e(TAG, "Failed to remove reservation from database");
        }

        return success;
    }
    public static boolean isReserved(Property property) {
        if (!isInitialized() || property == null) {
            return false;
        }

        try {
            return dbHelper.isReserved(currentUserEmail, property.id);
        } catch (Exception e) {
            Log.e(TAG, "Exception while checking reservation status", e);
            return false;
        }
    }

    public static ArrayList<Reservation> getReservations() {
        if (!isInitialized()) {
            Log.e(TAG, "ReservationManager not initialized!");
            return new ArrayList<>();
        }

        refreshReservations();
        return new ArrayList<>(userReservations);
    }

    public static ArrayList<Reservation> getAllReservations() {
        if (dbHelper == null) {
            return new ArrayList<>();
        }

        try {
            return dbHelper.getAllReservations();
        } catch (Exception e) {
            Log.e(TAG, "Exception getting all reservations", e);
            return new ArrayList<>();
        }
    }
    private static void loadUserReservations() {
        if (!isInitialized()) {
            return;
        }
        if (userReservations == null) {
            userReservations = new ArrayList<>();
        } else {
            userReservations.clear();
        }

        try {
            ArrayList<Reservation> freshReservations = dbHelper.getUserReservations(currentUserEmail);
            if (freshReservations != null && !freshReservations.isEmpty()) {
                userReservations.addAll(freshReservations);
                Log.d(TAG, "Loaded " + userReservations.size() + " reservations from database");
            } else {
                Log.d(TAG, "No reservations found in database for user: " + currentUserEmail);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading user reservations", e);
        }
    }

    public static void refreshReservations() {
        if (isInitialized()) {
            loadUserReservations();
        }
    }

    public static void clearInstance() {
        Log.d(TAG, "Clearing ReservationManager instance");
        if (userReservations != null) {
            userReservations.clear();
        }
        currentUserEmail = null;
        if (dbHelper != null) {
            dbHelper.close();
        }
        dbHelper = null;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }
}