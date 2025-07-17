package com.example.project;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class FavoritesManager {
    private static final String TAG = "FavoritesManager";
    private static FavoritesManager instance;
    private static DBHelper dbHelper;
    private static String userEmail;

    private static ArrayList<Favorite> userFavereat = new ArrayList<>();

    private FavoritesManager() {
    }

    public static void initialize(Context context, String email) {
        if (instance == null) {
            instance = new FavoritesManager();
        }
        dbHelper = DBHelper.getInstance(context);
        userEmail = email;
    }

   public static ArrayList<Property> getFavorites() {
        if (dbHelper == null || userEmail == null) {
            return new ArrayList<>();
        }

        ArrayList<Favorite> favoriteObjects = dbHelper.getUserFavorites(userEmail);
        ArrayList<Property> favoriteProperties = new ArrayList<>();

        for (Favorite fav : favoriteObjects) {
            if (fav.property != null) {
                favoriteProperties.add(fav.property);
            }
        }

        Log.d(TAG, "Retrieved " + favoriteProperties.size() + " favorite properties from database");
        return favoriteProperties;
    }

    public static boolean addToFavorites(Property property) {
        if (dbHelper == null || userEmail == null) {
            Log.e(TAG, "FavoritesManager not initialized");
            return false;
        }
        Property existing = dbHelper.getPropertyById(property.id);
        if (existing == null) {
            Log.w(TAG, "testttttttttttttttttttttt Property not found in database, inserting it first");
            long insertResult = dbHelper.insertProperty(property);
            if (insertResult == -1) {
                Log.e(TAG, " Failed to insert property before adding to favorites");
                return false;
            }
        }

        boolean result = dbHelper.addToFavorites(userEmail, property.id);
        Log.d(TAG, "Add to favorites - Property: " + property.title + ", Result: " + result);
        return result;
    }



    public static boolean removeFromFavorites(Property property) {
        if (dbHelper == null || userEmail == null) {
            Log.e(TAG, "FavoritesManager not initialized");
            return false;
        }

        boolean result = dbHelper.removeFromFavorites(userEmail, property.id);
        Log.d(TAG, "Remove from favorites - Property: " + property.title + ", Result: " + result);
        return result;
    }

    public static boolean isFavorite(Property property) {
        if (dbHelper == null || userEmail == null) {
            Log.e(TAG, "FavoritesManager not initialized");
            return false;
        }

        return dbHelper.isFavorite(userEmail, property.id);
    }

    public static void clearInstancee() {
        Log.d(TAG, "Clearing Favorites instance");
        if (userFavereat != null) {
            userFavereat.clear();
        }
        userEmail = null;
        if (dbHelper != null) {
            dbHelper.close();
        }
        dbHelper = null;
    }
}