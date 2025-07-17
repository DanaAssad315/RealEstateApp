package com.example.project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import java.util.ArrayList;
import java.util.Locale;

public class PropertiesFragment extends Fragment {
    private String currentUserEmail;
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private ProgressBar progressBar;
    private ArrayList<Property> properties = new ArrayList<>();
    private ArrayList<Property> filteredList = new ArrayList<>();


    private EditText editTextPrice, editTextLocation;
    private Spinner spinnerType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState  ) {
        View view = inflater.inflate(R.layout.fragment_properties, container, false);
        currentUserEmail = getCurrentUserEmail();

        if (currentUserEmail != null && getContext() != null) {
            ReservationManager.initialize(getContext(), currentUserEmail);
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        editTextPrice = view.findViewById(R.id.editText_price);
        editTextLocation = view.findViewById(R.id.editText_location);
        spinnerType = view.findViewById(R.id.spinner_type);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PropertyAdapter(filteredList);
        recyclerView.setAdapter(adapter);
        setupFilters();
        new ConnectionAsyncTask(this).execute("https://mocki.io/v1/705ada5b-062b-4d0d-a017-112ad0f89c4c");
        return view;
    }

    private void setupFilters() {

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"All", "Apartment", "Villa", "Land"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        editTextPrice.addTextChangedListener(watcher);
        editTextLocation.addTextChangedListener(watcher);

        spinnerType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    public void applyFilters() {
        String maxPriceStr = editTextPrice.getText().toString();
        String location = editTextLocation.getText().toString().toLowerCase(Locale.ROOT);
        String selectedType = spinnerType.getSelectedItem().toString();

        filteredList.clear();

        for (Property p : properties) {
            boolean matchesPrice = true, matchesLocation = true, matchesType = true;

            if (!maxPriceStr.isEmpty()) {
                try {
                    double maxPrice = Double.parseDouble(maxPriceStr);
                    matchesPrice = p.price <= maxPrice;
                } catch (NumberFormatException e) {
                    matchesPrice = true;
                }
            }

            if (!location.isEmpty()) {
                matchesLocation = p.location.toLowerCase(Locale.ROOT).contains(location);
            }

            if (!selectedType.equals("All")) {
                matchesType = p.type.equalsIgnoreCase(selectedType);
            }

            if (matchesPrice && matchesLocation && matchesType) {
                filteredList.add(p);
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    public void displayProperties(ArrayList<Property> data) {
        properties.clear();
        properties.addAll(data);
        applyFilters();
    }


    private String getCurrentUserEmail() {
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            return prefs.getString("user_email", null);
        }
        return null;
    }
}
