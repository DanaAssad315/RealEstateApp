package com.example.project;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {
    private static final String TAG = "ReservationAdapter";
    private ArrayList<Reservation> reservations;
    private OnReservationDeletedListener listener;

    public interface OnReservationDeletedListener {
        void onReservationDeleted();
    }

    public ReservationAdapter(ArrayList<Reservation> reservations) {
        this.reservations = reservations != null ? reservations : new ArrayList<>();
        Log.d(TAG, "Adapter created with " + this.reservations.size() + " reservations");
    }

    public void setOnReservationDeletedListener(OnReservationDeletedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position >= reservations.size()) {
            Log.w(TAG, "Position " + position + " is out of bounds for size " + reservations.size());
            return;
        }
        Reservation reservation = reservations.get(position);

        if (reservation == null) {
            setEmptyData(holder);
            return;
        }

        if (reservation.property != null) {
            holder.titleTextView.setText(reservation.property.title);
            holder.locationTextView.setText(reservation.property.location);
            holder.priceTextView.setText("$" + reservation.property.price);
            holder.timeTextView.setText("Reservation Date: " + reservation.getFormattedReservationDate());
            holder.createdAtTextView.setText("Created at: " + reservation.getFormattedCreatedAt());
        } else {
            Log.w(TAG, "Property is null for reservation ID: " + reservation.id);
            setEmptyData(holder);
        }

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < reservations.size()) {
                showDeleteConfirmationDialog(v, currentPosition);
            } else {
                Log.w(TAG, "Invalid position for delete: " + currentPosition);
            }
        });
    }


    private void setEmptyData(ViewHolder holder) {
        holder.titleTextView.setText("No property data available");
        holder.locationTextView.setText("-");
        holder.priceTextView.setText("-");
        holder.timeTextView.setText("-");
    }

    private void showDeleteConfirmationDialog(View view, int position) {
        if (position >= reservations.size()) {
            return;
        }

        Reservation reservation = reservations.get(position);
        String propertyTitle = (reservation.property != null && reservation.property.title != null)
                ? reservation.property.title : "this property";

        new AlertDialog.Builder(view.getContext())
                .setTitle("Delete Reservation")
                .setMessage("Are you sure you want to delete the reservation for \"" + propertyTitle + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> deleteReservation(view, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReservation(View view, int position) {
        if (position >= reservations.size()) {
            Log.w(TAG, "Cannot delete reservation at invalid position: " + position);
            return;
        }

        Reservation reservation = reservations.get(position);

        boolean success = ReservationManager.removeReservation(reservation.id);
        if (success) {
            reservations.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, reservations.size());

            Toast.makeText(view.getContext(), "Reservation deleted successfully", Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onReservationDeleted();
            }
        } else {
            Toast.makeText(view.getContext(), "Failed to delete reservation", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        int count = (reservations != null) ? reservations.size() : 0;
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, locationTextView, priceTextView, timeTextView;
        TextView createdAtTextView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.reservation_title);
            locationTextView = itemView.findViewById(R.id.reservation_location);
            priceTextView = itemView.findViewById(R.id.reservation_price);
            timeTextView = itemView.findViewById(R.id.reservation_time);
            createdAtTextView = itemView.findViewById(R.id.reservation_created_at);
            deleteButton = itemView.findViewById(R.id.btn_delete_reservation);
        }
    }

}