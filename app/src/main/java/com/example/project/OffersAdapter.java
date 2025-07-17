package com.example.project;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {

    private List<Offer> offersList;

    public OffersAdapter(List<Offer> offersList) {
        this.offersList = offersList;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new OfferViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offersList.get(position);
        holder.title.setText(offer.getTitle());
        holder.subtitle.setText(
                "Property ID: " + offer.getPropertyId() + "\n" +
                         "Offer description: " + offer.getDescription() + "\n" +
                        offer.getStartDate() + " to " + offer.getEndDate()
        );

    }

    @Override
    public int getItemCount() {
        return offersList.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}