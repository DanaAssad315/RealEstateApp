package com.example.project;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CreateOfferFragment extends Fragment {

    private EditText editPropertyId, editTitle, editDescription, editStartDate, editEndDate;

    private DBHelper_Offers dbHelper;
    private OffersAdapter offersAdapter;
    private ArrayList<Offer> offersList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_offer, container, false);

        editPropertyId = view.findViewById(R.id.editPropertyId);
        editTitle = view.findViewById(R.id.editTitle);
        editDescription = view.findViewById(R.id.editOfferDescription);
        editStartDate = view.findViewById(R.id.editStartDate);
        editEndDate = view.findViewById(R.id.editEndDate);
        Button btnCreateOffer = view.findViewById(R.id.btnCreateOffer);
        RecyclerView recyclerOffers = view.findViewById(R.id.recyclerOffers);

        dbHelper = new DBHelper_Offers(getContext());

        offersList = new ArrayList<>();
        offersAdapter = new OffersAdapter(offersList);
        recyclerOffers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerOffers.setAdapter(offersAdapter);

        loadOffersFromDB();

        btnCreateOffer.setOnClickListener(v -> {
            if (validateInput()) {
                int propertyId = Integer.parseInt(editPropertyId.getText().toString().trim());
                String title = editTitle.getText().toString().trim();
                String description = editDescription.getText().toString().trim();
                String startDate = editStartDate.getText().toString().trim();
                String endDate = editEndDate.getText().toString().trim();

                boolean inserted = dbHelper.addSpecialOffer(propertyId, title, description, startDate, endDate);

                if (inserted) {
                    Toast.makeText(getContext(), "Offer created successfully", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadOffersFromDB();
                } else {
                    Toast.makeText(getContext(), "Failed to create offer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(editPropertyId.getText())) {
            editPropertyId.setError("Property ID is required");
            editPropertyId.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(editTitle.getText())) {
            editTitle.setError("Title is required");
            editTitle.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(editStartDate.getText())) {
            editStartDate.setError("Start Date is required");
            editStartDate.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(editEndDate.getText())) {
            editEndDate.setError("End Date is required");
            editEndDate.requestFocus();
            return false;
        }
        return true;
    }

    private void clearInputs() {
        editPropertyId.setText("");
        editTitle.setText("");
        editDescription.setText("");
        editStartDate.setText("");
        editEndDate.setText("");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadOffersFromDB() {
        offersList.clear();

        Cursor cursor = dbHelper.getAllSpecialOffers();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("propertyId"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));

                offersList.add(new Offer(id, propertyId, title, description, startDate, endDate));
            }
            cursor.close();
        }
        offersAdapter.notifyDataSetChanged();
    }
}