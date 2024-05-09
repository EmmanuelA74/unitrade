package com.example.unitrade;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.unitrade.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignInActivity extends Activity {

    private ActivitySignInBinding binding;

    private FirebaseAuth mAuth;

    private final View.OnClickListener button_login_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = binding.editTextText.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Success!",
                                        Toast.LENGTH_SHORT).show();

                                Intent newIntent = new Intent(SignInActivity.this, HomeActivity.class);
                                String userName = MySingleton.getInstance().getMyVariable();
                                newIntent.putExtra("USERNAME", userName);
                                startActivity(newIntent);
                                finish();
                            }
                            else {
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        binding.btnLogin.setOnClickListener(button_login_clickListener);

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