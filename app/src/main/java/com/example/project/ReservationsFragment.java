package com.example.project;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ReservationsFragment extends Fragment implements ReservationAdapter.OnReservationDeletedListener {

    private static final String TAG = "ReservationsFragment";

    private RecyclerView reservationsRecyclerView;
    private ReservationAdapter reservationAdapter;
    private TextView emptyStateTextView;
    private LinearLayout emptyStateLayout;
    private ProgressBar loadingProgressBar;

    private ArrayList<Reservation> reservationsList;
    private Handler mainHandler;
    private boolean isLoading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ReservationsFragment created");
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        loadReservations();
    }

    private void initViews(View view) {
        try {
            reservationsRecyclerView = view.findViewById(R.id.reservations_recycler_view);
            emptyStateTextView = view.findViewById(R.id.empty_state_text);
            emptyStateLayout = view.findViewById(R.id.empty_state_layout);
            loadingProgressBar = view.findViewById(R.id.loading_progress_bar);
            if (reservationsRecyclerView == null) {
                Log.e(TAG, "CRITICAL ERROR: RecyclerView not found!");
            }
            if (emptyStateLayout == null) {
                Log.e(TAG, "ERROR: EmptyStateLayout not found!");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
    }

    private void setupRecyclerView() {
        if (reservationsRecyclerView == null) {
            Log.e(TAG, "Cannot setup RecyclerView - it's null!");
            return;
        }

        try {
            reservationsList = new ArrayList<>();
            reservationAdapter = new ReservationAdapter(reservationsList);
            reservationAdapter.setOnReservationDeletedListener(this);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            reservationsRecyclerView.setLayoutManager(layoutManager);
            reservationsRecyclerView.setAdapter(reservationAdapter);
            reservationsRecyclerView.setHasFixedSize(true);

            Log.d(TAG, "RecyclerView setup complete");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
        }
    }

    private void loadReservations() {
        if (isLoading) {
            Log.d(TAG, "Already loading reservations, skipping...");
            return;
        }
        isLoading = true;
        Log.d(TAG, "=== Starting to Load Reservations ===");
        showLoadingState();
        new Thread(() -> {
            try {
                if (!ReservationManager.isInitialized()) {
                    mainHandler.post(() -> {
                        hideLoadingState();
                        Toast.makeText(getContext(), "Error: Reservation system not initialized", Toast.LENGTH_SHORT).show();
                        showEmptyState();
                        isLoading = false;
                    });
                    return;
                }

                String currentUser = ReservationManager.getCurrentUserEmail();
                Log.d(TAG, "Loading reservations for user: " + currentUser);

                ReservationManager.refreshReservations();
                ArrayList<Reservation> reservations = ReservationManager.getReservations();

                Log.d(TAG, "Retrieved " + (reservations != null ? reservations.size() : 0) + " reservations");

                mainHandler.post(() -> {
                    try {
                        updateReservationsList(reservations);
                        hideLoadingState();
                        isLoading = false;
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating UI", e);
                        handleLoadingError(e);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error loading reservations in background", e);
                mainHandler.post(() -> handleLoadingError(e));
            }
        }).start();
    }

    private void updateReservationsList(ArrayList<Reservation> reservations) {
        if (reservationsList == null) {
            reservationsList = new ArrayList<>();
        }
        reservationsList.clear();
        if (reservations != null && !reservations.isEmpty()) {
            reservationsList.addAll(reservations);
            Log.d(TAG, "Processing " + reservationsList.size() + " reservations");
        } else {
            Log.d(TAG, "No reservations found");
        }
        if (reservationAdapter != null) {
            reservationAdapter.notifyDataSetChanged();
            Log.d(TAG, "Adapter updated with " + reservationsList.size() + " items");
        } else {
            Log.e(TAG, "ReservationAdapter is null!");
        }

        updateUIState();
    }

    private void updateUIState() {
        boolean hasReservations = reservationsList != null && !reservationsList.isEmpty();
        Log.d(TAG, "Updating UI state - Has reservations: " + hasReservations);

        if (hasReservations) {
            showReservationsList();
        } else {
            showEmptyState();
        }
    }

    private void showLoadingState() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
        if (reservationsRecyclerView != null) {
            reservationsRecyclerView.setVisibility(View.GONE);
        }
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.GONE);
        }
        Log.d(TAG, "Loading state shown");
    }

    private void hideLoadingState() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.GONE);
        }
    }

    private void showEmptyState() {
        Log.d(TAG, "Showing empty state");

        if (reservationsRecyclerView != null) {
            reservationsRecyclerView.setVisibility(View.GONE);
        }
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
        if (emptyStateTextView != null) {
            emptyStateTextView.setText("No reservations yet\n\nStart exploring available properties\nto make your first reservation!");
        }
    }
    private void showReservationsList() {
        Log.d(TAG, "Showing reservations list with " +
                (reservationsList != null ? reservationsList.size() : 0) + " items");

        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.GONE);
        }
        if (reservationsRecyclerView != null) {
            reservationsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void handleLoadingError(Exception e) {
        Log.e(TAG, "Handling loading error", e);
        hideLoadingState();
        isLoading = false;

        String errorMessage = "Error loading reservations";
        if (e != null && e.getMessage() != null) {
            errorMessage += ": " + e.getMessage();
        }

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        showEmptyState();
    }

    @Override
    public void onReservationDeleted() {
        Log.d(TAG, "Reservation deleted callback received");

        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
        if (mainHandler != null) {
            mainHandler.postDelayed(() -> {
                loadReservations();
                Toast.makeText(getContext(), "Reservation deleted successfully", Toast.LENGTH_SHORT).show();
            }, 300);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed - refreshing data");

        if (mainHandler != null) {
            mainHandler.postDelayed(() -> {
                if (isAdded() && getContext() != null) {
                    loadReservations();
                }
            }, 500);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        isLoading = false;
    }
}