package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private FavoritesAdapter adapter;
    private ArrayList<Favorite> favorites;

    public FavoritesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.favorites_recycler);
        emptyTextView = view.findViewById(R.id.empty_favorites_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        String email = sharedPrefManager.readString("email", "");

        if (email != null && !email.isEmpty()) {
            FavoritesManager.initialize(requireContext(), email);
            loadFavorites();
        } else {
            Log.e("FavoritesFragment", " No email found in SharedPrefManager. Favorites won't load.");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites(); // Refresh when fragment becomes visible
    }

    private void loadFavorites() {
        ArrayList<Property> propertyList = FavoritesManager.getFavorites();
        favorites = new ArrayList<>();

        for (Property p : propertyList) {
            favorites.add(new Favorite(p));
        }

        if (favorites.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);

            adapter = new FavoritesAdapter(favorites, this);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }
}
