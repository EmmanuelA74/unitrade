package com.example.unitrade;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.unitrade.databinding.FragmentHomeBinding;
import com.example.unitrade.databinding.FragmentListingBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ListingFragment extends Fragment {

    private FragmentListingBinding binding;

    // URI to store the selected image URI
    private Uri selectedImageUri;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListingBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        // Load the spinner
        int strArrId = getResources().getIdentifier("condition", "array", getActivity().getPackageName());
        String[] conditionsArray = getResources().getStringArray(strArrId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, conditionsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCondition.setAdapter(adapter);


        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        selectedImageUri = uri;

                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        // Set up OnClickListener for the buttonSubmit
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve data from the views
                String itemName = binding.editTextItemName.getText().toString();
                String itemDescription = binding.editTextItemDescription.getText().toString();
                String condition = binding.spinnerCondition.getSelectedItem().toString();
                double price = Double.parseDouble(binding.editTextPrice.getText().toString());
                String deliveryType = binding.rbDelivery.isChecked() ? "Delivery" : "Pickup";

                // Check if an image is selected
                if (selectedImageUri == null) {
                    Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get a reference to the Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("listing_images");

                // Create a unique filename for the image
                String imageName = UUID.randomUUID().toString() + ".jpg";

                // Get a reference to the location where the image will be saved in Firebase Storage
                StorageReference imageRef = storageRef.child(imageName);

                // Upload the image to Firebase Storage
                imageRef.putFile(selectedImageUri)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Return the download URL of the uploaded image
                            return imageRef.getDownloadUrl();
                        })
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Image uploaded successfully
                                Uri downloadUri = task.getResult();
                                String myUri = downloadUri.toString();
                                // Create a new Listing object with the image URI
                                Listing listing = new Listing(itemName, itemDescription, condition, price, deliveryType, myUri, false, "No reviews yet.");


                                // Upload the listing object to Firebase Realtime Database
                                DatabaseReference listingsRef = FirebaseDatabase.getInstance().getReference("listings");
                                String listingId = listingsRef.push().getKey(); // Generate a unique key for the listing


                                if (listingId != null) {

                                    listingsRef.child(listingId).setValue(listing)
                                            .addOnSuccessListener(aVoid -> {
                                                // Listing uploaded successfully
                                                Toast.makeText(getContext(), "Listing uploaded successfully", Toast.LENGTH_SHORT).show();

                                                //Return to Home Fragment
                                                HomeFragment newFrag = new HomeFragment();
                                                requireActivity().getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.frameLayout, newFrag, "findThisFrag").addToBackStack(null).commit();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Error uploading listing
                                                Toast.makeText(getContext(), "Failed to upload listing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });


                                } else {
                                    // Handle the case where listingId is null
                                    Log.e("Firebase", "Failed to generate a unique key for the listing");
                                }
                        }});
            }
        });

        // Set up OnClickListener for the buttonUploadImage
        binding.buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        return binding.getRoot();
    }


}
