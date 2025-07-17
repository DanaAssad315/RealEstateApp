package com.example.project;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private ArrayList<Favorite> favorites;
    private FavoritesFragment fragment;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_item, parent, false);
        return new ViewHolder(view);
    }
    public FavoritesAdapter(ArrayList<Favorite> favorites, FavoritesFragment fragment) {
        this.favorites = favorites;
        this.fragment = fragment;
    }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Favorite favorite = favorites.get(position);
            Property property = favorite.property;

            if (favorite.property != null && favorite.property.getImageUrl() != null && !favorite.property.getImageUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(favorite.property.getImageUrl())
                        .into(holder.propertyImageView);
            }
            if (property != null) {
    //
                Glide.with(holder.itemView.getContext())
                        .load(property.image_url)
                        .error(R.drawable.error_image)
                        .into(holder.propertyImageView);

                holder.titleTextView.setText(property.title);
                holder.priceTextView.setText("$" + property.price);
                holder.locationTextView.setText(property.location);
                holder.descriptionTextView.setText(property.description);


            } else {
                holder.propertyImageView.setImageResource(R.drawable.error_image);
                holder.titleTextView.setText("No property info");
                holder.priceTextView.setText("");
                holder.locationTextView.setText("");
                holder.descriptionTextView.setText("");

            }

            holder.buttonFavorite.setImageResource(R.drawable.heart_filled);

            holder.buttonFavorite.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Favorite selectedFavorite = favorites.get(currentPosition);
                    if (selectedFavorite.property != null) {
                        FavoritesManager.removeFromFavorites(selectedFavorite.property);
                    }
                    favorites.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, favorites.size());
                    Toast.makeText(v.getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            });

            holder.buttonReserve.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Favorite reservedFavorite = favorites.get(currentPosition);
                    if (reservedFavorite.property != null) {
                        ReservationManager.addReservation(reservedFavorite.property);
                        Toast.makeText(v.getContext(), "Property Reserved from favorites!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, priceTextView, locationTextView, descriptionTextView;
        ImageView propertyImageView;
        ImageButton buttonFavorite, buttonReserve;

        public ViewHolder(View itemView) {
            super(itemView);
            propertyImageView = itemView.findViewById(R.id.property_image);

            titleTextView = itemView.findViewById(R.id.property_title);
            priceTextView = itemView.findViewById(R.id.property_price);
            locationTextView = itemView.findViewById(R.id.property_location);
            descriptionTextView = itemView.findViewById(R.id.property_description);
            buttonFavorite = itemView.findViewById(R.id.btn_favorite);
            buttonReserve = itemView.findViewById(R.id.btn_reserve);
        }
    }
}
