package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {
    private final ArrayList<Property> properties;

    public PropertyAdapter(ArrayList<Property> properties) {
        this.properties = properties;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.titleTextView.setText(property.title);
        holder.priceTextView.setText(String.format("$%.2f", property.price));
        holder.locationTextView.setText(property.location);
        holder.descriptionTextView.setText(property.description);

        Glide.with(holder.itemView.getContext())
                .load(property.image_url)
                .error(R.drawable.error_image)
                .into(holder.propertyImageView);


        updateFavoriteButton(holder, property);

        updateReserveButton(holder, property);


        holder.buttonFavorite.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Property selectedProperty = properties.get(currentPosition);
                handleFavoriteClick(v, selectedProperty, holder);
            }
        });

        holder.buttonReserve.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Property selectedProperty = properties.get(currentPosition);
                handleReserveClick(v, selectedProperty, holder);
            }
        });
    }

    private void handleFavoriteClick(View view, Property property, ViewHolder holder) {
        try {
            if (FavoritesManager.isFavorite(property)) {
                FavoritesManager.removeFromFavorites(property);
                Toast.makeText(view.getContext(), "Delet of Favorite", Toast.LENGTH_SHORT).show();
            } else {
                FavoritesManager.addToFavorites(property);
                Toast.makeText(view.getContext(), "Add to Favorite", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteButton(holder, property);
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "error in the system", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleReserveClick(View view, Property property, ViewHolder holder) {

        try {

            if (ReservationManager.isReserved(property)) {
                Toast.makeText(view.getContext(), "This property has been booked by!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = ReservationManager.addReservation(property);
            if (success) {
                Toast.makeText(view.getContext(), "sacussessfullll recevied !", Toast.LENGTH_SHORT).show();
                updateReserveButton(holder, property);

            } else {
                Toast.makeText(view.getContext(), "Reservation failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Error in the reservation process: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavoriteButton(ViewHolder holder, Property property) {
        try {
            if (FavoritesManager.isFavorite(property)) {
                holder.buttonFavorite.setImageResource(R.drawable.heart_filled);
            } else {
                holder.buttonFavorite.setImageResource(R.drawable.heart_outline);
            }
        } catch (Exception e) {

            holder.buttonFavorite.setImageResource(R.drawable.heart_outline);
        }
    }

    private void updateReserveButton(ViewHolder holder, Property property) {
        try {
            if (ReservationManager.isInitialized() && ReservationManager.isReserved(property)) {
                holder.buttonReserve.setEnabled(false);
                holder.buttonReserve.setAlpha(0.5f);
            } else {
                holder.buttonReserve.setEnabled(true);
                holder.buttonReserve.setAlpha(1.0f);
            }
        } catch (Exception e) {
            holder.buttonReserve.setEnabled(true);
            holder.buttonReserve.setAlpha(1.0f);
        }
    }


    @Override
    public int getItemCount() {
        return properties.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, priceTextView, locationTextView, descriptionTextView;
        ImageView propertyImageView;
        ImageButton buttonReserve, buttonFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.property_title);
            priceTextView = itemView.findViewById(R.id.property_price);
            locationTextView = itemView.findViewById(R.id.property_location);
            descriptionTextView = itemView.findViewById(R.id.property_description);
            propertyImageView = itemView.findViewById(R.id.property_image);
            buttonFavorite = itemView.findViewById(R.id.btn_favorite);
            buttonReserve = itemView.findViewById(R.id.btn_reserve);
        }
    }
}