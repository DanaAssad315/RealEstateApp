package com.example.project;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeaturedFragment extends Fragment {

    private RecyclerView recyclerView;
    private OffersAdapter adapter;
    private ArrayList<Offer> offerList;

    public FeaturedFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_featured, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_offers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        offerList = new ArrayList<>();
        loadOffersFromDatabase();

        adapter = new OffersAdapter(offerList);
        recyclerView.setAdapter(adapter);
    }

    private void loadOffersFromDatabase() {
        DBHelper_Offers dbHelper = new DBHelper_Offers(getContext());
        Cursor cursor = dbHelper.getAllSpecialOffers();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("propertyId"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));

                Offer offer = new Offer(id, propertyId, title, description, startDate, endDate);
                offerList.add(offer);
            } while (cursor.moveToNext());

            cursor.close();
        }
    }
}
