package com.example.unitrade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unitrade.databinding.FragmentListingBinding;
import com.example.unitrade.databinding.FragmentListingDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class ListingDetailsFragment extends Fragment {

    private FragmentListingDetailsBinding binding;
    private DatabaseReference listingsRef;
    private FirebaseAuth mAuth;

    private String localID = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListingDetailsBinding.inflate(inflater, container, false);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        listingsRef = FirebaseDatabase.getInstance().getReference("listings");

        // Retrieve data from bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("name", "");
            String description = bundle.getString("description", "");
            String condition = bundle.getString("condition", "");
            double price = bundle.getDouble("price", 0.0);
            String deliveryType = bundle.getString("deliveryType", "");
            String imgUrl = bundle.getString("imgUrl", "");
            String listingId = getArguments().getString("listingId");
            String review = bundle.getString("review", "No reviews yet");



            // Set data to views
            binding.textViewListingName.setText(name);
            binding.textViewListingDescription.setText(description);
            binding.textViewListingCondition.setText(condition);
            binding.textViewListingPrice.setText(String.valueOf(price));
            binding.textViewListingDeliveryType.setText(deliveryType);
            binding.textViewReview.setText(review);

            localID = listingId;

            // Load image using Glide or any other image loading library
            Glide.with(requireContext()).load(imgUrl).into(binding.imageViewListing);
        }

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Get the listing ID associated with this listing
                    String listingId = localID;

                    // Create a map to hold the buyer ID
                    Map<String, Object> buyerIdMap = new HashMap<>();
                    buyerIdMap.put("buyerId", userId);

                    // Upload buyer ID to the listing in Firebase database
                    listingsRef.child(listingId).updateChildren(buyerIdMap)
                            .addOnSuccessListener(aVoid -> {

                                // Buyer ID uploaded successfully
                                Toast.makeText(getContext(), "Item bought!", Toast.LENGTH_SHORT).show();

                                // Create an AlertDialog asking if the buyer wants to give a review
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setTitle("Review Seller")
                                        .setMessage("Would you like to give a review to the seller?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Show another AlertDialog for entering the review
                                                AlertDialog.Builder reviewDialogBuilder = new AlertDialog.Builder(requireContext());
                                                reviewDialogBuilder.setTitle("Leave a Review");
                                                // Add an EditText for the user to enter their review
                                                final EditText input = new EditText(requireContext());
                                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                                reviewDialogBuilder.setView(input);

                                                // Set up the buttons for the review dialog
                                                reviewDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Get the review text entered by the user
                                                        String reviewText = input.getText().toString().trim();
                                                        // Add the review to the database with the associated seller ID
                                                        if (!reviewText.isEmpty()) {
                                                            addReviewToDatabase(reviewText, localID);
                                                        }
                                                    }
                                                });
                                                reviewDialogBuilder.setNegativeButton("Cancel", null);
                                                reviewDialogBuilder.show();
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();


                                HomeFragment newFrag = new HomeFragment();
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frameLayout, newFrag, "findThisFrag").addToBackStack(null).commit();
                            })
                            .addOnFailureListener(e -> {
                                // Error uploading buyer ID
                                Toast.makeText(getContext(), "Failed to buy item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // User not authenticated
                    Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageFragment newFrag = new MessageFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, newFrag, "findThisFrag").addToBackStack(null).commit();
            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the listing ID associated with this listing
                String listingId = localID;

                // Create a map to hold the buyer ID
                Map<String, Object> cartStatusMap = new HashMap<>();
                cartStatusMap.put("addedtoCart", true);

                listingsRef.child(listingId).updateChildren(cartStatusMap).addOnSuccessListener(aVoid -> {
                            // Cart Status updated successfully
                            Toast.makeText(getContext(), "Item added to Cart!", Toast.LENGTH_LONG).show();
                            HomeFragment newFrag = new HomeFragment();
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frameLayout, newFrag, "findThisFrag").addToBackStack(null).commit();
                        })
                        .addOnFailureListener(e -> {
                            // Error uploading buyer ID
                            Toast.makeText(getContext(), "Failed to add item to cart" + e.getMessage(), Toast.LENGTH_LONG).show();
                        });

            }
        });




        return binding.getRoot();
    }

    private void addReviewToDatabase(String reviewText, String listingId) {
        // Get a reference to the listings node in the database
        DatabaseReference listingsRef = FirebaseDatabase.getInstance().getReference("listings");

        // Create a map to update the review field of the listing
        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("review", reviewText);

        // Update the review field of the listing with the provided listing ID
        listingsRef.child(listingId).updateChildren(reviewMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Review added successfully
                        Toast.makeText(getContext(), "Review added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add review
                        Toast.makeText(getContext(), "Failed to add review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
