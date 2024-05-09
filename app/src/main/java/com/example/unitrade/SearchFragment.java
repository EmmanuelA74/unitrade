package com.example.unitrade;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.unitrade.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    private ListingAdapter adapter;
    private List<Listing> listings;
    private List<String> listingIds;
    private DatabaseReference listingsRef;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = binding.recyclerView3;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        binding.buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        return binding.getRoot();
    }

    public void performSearch(){
        String minPriceStr = binding.editTextPriceMin.getText().toString();
        String maxPriceStr = binding.editTextPriceMax.getText().toString();
        String condition = binding.editTextCondition.getText().toString();
        String deliveryType = binding.editTextDeliveryType.getText().toString();

        // Parse the price range
        double minPrice = TextUtils.isEmpty(minPriceStr) ? Double.MIN_VALUE : Double.parseDouble(minPriceStr);
        double maxPrice = TextUtils.isEmpty(maxPriceStr) ? Double.MAX_VALUE : Double.parseDouble(maxPriceStr);

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

                    if (listing != null) {
                        // Filter the listings based on the input criteria
                        if ((listing.getPrice() >= minPrice && listing.getPrice() <= maxPrice)
                                && (TextUtils.isEmpty(condition) || listing.getItemCondition().equalsIgnoreCase(condition))
                                && (TextUtils.isEmpty(deliveryType) || listing.getDeliveryType().equalsIgnoreCase(deliveryType))) {
                            listings.add(listing);
                        }
                    }



                }
                // Update the RecyclerView with the retrieved listings
                adapter.setListings(listings);

                if (listings.isEmpty()){
                    Toast.makeText(getContext(), "No results found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
                Log.e("Firebase", "Error retrieving search results: " + error.getMessage());
            }
        });
    }
}