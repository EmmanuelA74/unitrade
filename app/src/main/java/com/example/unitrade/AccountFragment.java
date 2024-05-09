package com.example.unitrade;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.unitrade.databinding.FragmentAccountBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private FirebaseAuth mAuth;

    ArrayList<String> acctDetails = new ArrayList<String>();
    ArrayAdapter arrayAdpt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        acctDetails.add("Cart");

        arrayAdpt = new ArrayAdapter<>(requireContext(),  android.R.layout.simple_list_item_1, acctDetails);
        binding.listView.setAdapter(arrayAdpt);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CartFragment newFrag = new CartFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, newFrag, "findThisFrag").addToBackStack(null).commit();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        binding.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent intent = new Intent(getContext(), SignInActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}