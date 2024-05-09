package com.example.unitrade;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.unitrade.databinding.FragmentMessageBinding;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;



public class MessageFragment extends Fragment {
    private FragmentMessageBinding binding;


    ArrayList<String> listOfDiscussion = new ArrayList<String>();
    ArrayAdapter arrayAdpt;

    String UserName = "";

    private DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().getRoot();
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMessageBinding.inflate(inflater,container,false);

        arrayAdpt = new ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1, listOfDiscussion);
        binding.lvDiscussionTopics.setAdapter(arrayAdpt);






        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String temp = dataSnapshot.getValue(String.class);
                        UserName = temp;
                        // Use the username as needed
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }





        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the "Messages" child from the dataSnapshot
                DataSnapshot messagesSnapshot = dataSnapshot.child("Messages");

                // Clear the ArrayAdapter
                arrayAdpt.clear();

                // Check if the "Messages" node exists and add its key to the ArrayAdapter
                if (messagesSnapshot.exists()) {
                    arrayAdpt.add(messagesSnapshot.getKey());
                }

                // Notify the ArrayAdapter that the data set has changed
                arrayAdpt.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        binding.lvDiscussionTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(requireContext(), DiscussionActivity.class);
                i.putExtra("selected_topic", ((TextView)view).getText().toString());
                 i.putExtra("user_name", UserName);
                startActivity(i);
            }
        });



        return binding.getRoot();
    }

}