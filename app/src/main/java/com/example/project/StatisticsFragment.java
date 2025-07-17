
package com.example.project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        TextView textTotalUsers = view.findViewById(R.id.text_total_users);
        TextView menUser = view.findViewById(R.id.text_percentage_men);
        TextView womenUser = view.findViewById(R.id.text_percentage_women);

        TextView reserved_properties = view.findViewById(R.id.text_reserved_properties);
        TextView TopCountriestext = view.findViewById(R.id.text_top_countries);

        dbHelper = new DBHelper(getContext());

        int totalUsersCount = getTotalUsersCount();
        int malesCount = countMen();
        int femalesCount = countWomen();

        int totalReservations= dbHelper.getTotalReservationsCount();


        //1. Get Total Numebr of users (including the admins Number)
        textTotalUsers.setText(String.valueOf(totalUsersCount));

        //2. Get the Number of Men&Women of all users
        menUser.setText(String.valueOf(malesCount));
        womenUser.setText(String.valueOf(femalesCount));

        //3. Get the total number of reservation
        reserved_properties.setText(String.valueOf(totalReservations));

        // Get the 3-Top Top Reserving Countries
        ArrayList<String> topCountries = dbHelper.getTopReservingCountries(3);
        TopCountriestext.setText(TextUtils.join(", ", topCountries));
        return view;
    }
    private int getTotalUsersCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }


    public int countMen() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users WHERE LOWER(TRIM(gender)) = 'male'", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int countWomen() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users WHERE LOWER(TRIM(gender)) = 'female'", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}