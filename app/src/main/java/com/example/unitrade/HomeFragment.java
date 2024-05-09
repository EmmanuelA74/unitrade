package com.example.unitrade;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.unitrade.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener{


    private FragmentHomeBinding binding;
    private DatabaseReference listingsRef;
    private List<Listing> listings;
    private ListingAdapter adapter;

    private List<String> listingIds;
    private FirebaseAuth mAuth;



    private DatabaseReference messagesRef; // Reference for the messages database
    private final View.OnClickListener createListing_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ListingFragment newFrag = new ListingFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, newFrag, "findThisFrag").addToBackStack(null).commit();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        binding.button.setOnClickListener(createListing_clickListener);

        // Initialize RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize messages database reference
        messagesRef = FirebaseDatabase.getInstance().getReference("Messages");

        // Set up listener for new messages
        setupMessageListener();




        // Retrieve listings from Firebase and populate RecyclerView
        retrieveListingsFromFirebase();

        adapter.setOnItemClickListener(this);




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

                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Listing listing = dataSnapshot.getValue(Listing.class);
                    String listingId = dataSnapshot.getKey();

                    String buyerId = dataSnapshot.child("buyerId").getValue(String.class);
                    String sellerId = dataSnapshot.child("sellerId").getValue(String.class);

                    // Check if there's a buyer ID and if the current user is the buyer or seller
                    if (buyerId != null && (currentUserUid.equals(buyerId) || currentUserUid.equals(sellerId))) {
                        listings.add(listing);
                        listingIds.add(listingId);

                    } else if (buyerId == null) { // If there's no buyer ID, add the item regardless
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
                Log.e("Firebase", "Error retrieving listings: " + error.getMessage());
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Listing listing = listings.get(position);
        String name = listing.getItemName();
        String description = listing.getItemDescription();
        String condition = listing.getItemCondition();
        double price = listing.getPrice();
        String deliveryType = listing.getDeliveryType();
        String imgUrl = listing.getImageUrl();
        String listingId  = listingIds.get(position);
        String review = listing.getReview();



        Bundle bundle = new Bundle();
        bundle.putString("listingId", listingId);
        bundle.putString("name", name);
        bundle.putString("description", description);
        bundle.putString("condition", condition);
        bundle.putDouble("price", price);
        bundle.putString("deliveryType", deliveryType);
        bundle.putString("imgUrl", imgUrl);
        bundle.putString("review", review);


        ListingDetailsFragment detailsFragment = new ListingDetailsFragment();
        detailsFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupMessageListener() {
        // Add a ChildEventListener to listen for new messages in the database
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                // Call the method to display a notification when a new message is added
                //Toast.makeText(requireContext(), "New Message: You have a new message", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                // Handle changes to existing messages if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle message removal if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                // Handle child movement if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if needed
            }
        });
    }


}