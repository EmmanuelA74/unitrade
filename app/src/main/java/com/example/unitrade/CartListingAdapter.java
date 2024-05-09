package com.example.unitrade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.unitrade.databinding.CartItemListingBinding;

import java.util.List;

public class CartListingAdapter extends RecyclerView.Adapter<CartListingAdapter.ListingViewHolder> {

    private List<Listing> listings;
    private AdapterView.OnItemClickListener listener;



    public CartListingAdapter(List<Listing> listings) {
        this.listings = listings;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CartItemListingBinding binding = CartItemListingBinding.inflate(inflater, parent, false);
        return new ListingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.bind(listing);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(null, v, holder.getAdapterPosition(), 0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    // Set the listings data
    public void setListings(List<Listing> listings) {
        this.listings = listings;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }




    public static class ListingViewHolder extends RecyclerView.ViewHolder {

        private CartItemListingBinding binding;

        public ListingViewHolder(CartItemListingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Listing listing) {
            binding.textName.setText(listing.getItemName());
            binding.textPrice.setText(String.valueOf(listing.getPrice()));
            Glide.with(binding.getRoot())
                    .load(listing.getImageUrl())
                    .into(binding.imageView);
        }
    }
}
