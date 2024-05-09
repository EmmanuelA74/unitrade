package com.example.unitrade;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.unitrade.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends Activity {

    private ActivityRegisterBinding binding;

    private FirebaseAuth mAuth;

    private final View.OnClickListener button_signUp_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String email = binding.editTextText.getText().toString();
            String userName = binding.editTextText2.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Success!",
                                        Toast.LENGTH_SHORT).show();

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null) {
                                    String userId = currentUser.getUid();
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                    userRef.child("username").setValue(userName);
                                }

                                Intent newIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(newIntent);
                                finish();
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        binding.btnSignup.setOnClickListener(button_signUp_clickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }


}