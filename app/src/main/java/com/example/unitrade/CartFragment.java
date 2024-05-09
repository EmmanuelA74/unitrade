package com.example.unitrade;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.unitrade.databinding.FragmentCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {

    private FragmentCartBinding binding;

    private DatabaseReference listingsRef;
    private List<Listing> listings;
    private CartListingAdapter adapter;

    private List<String> listingIds;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = binding.recyclerView2;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartListingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Retrieve cart listings from Firebase and populate RecyclerView
        retrieveListingsFromFirebase();


        return binding.getRoot();
    }

    private void retrieveListingsFromFirebase() {
        // Assuming you have a Firebase database reference
        DatabaseReference listingsRef = FirebaseDatabase.getInstance().getReference("listings");

        // Add a ValueEventListener to retrieve data
        listingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listings = new ArrayList<>();
                listingIds = new ArrayList<String>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Listing listing = dataSnapshot.getValue(Listing.class);
                    String listingId = dataSnapshot.getKey();

                    Boolean cartStatus = dataSnapshot.child("addedtoCart").getValue(Boolean.class);

                    //Check if item has been added to cart
                    if (Boolean.TRUE.equals(cartStatus)){
                        listings.add(listing);
                        listingIds.add(listingId);
                    }

                }
                // Update the RecyclerView with the retrieved listings
                adapter.setListings(listings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
                Log.e("Firebase", "Error retrieving cart listings: " + error.getMessage());
            }
        });
    }
}