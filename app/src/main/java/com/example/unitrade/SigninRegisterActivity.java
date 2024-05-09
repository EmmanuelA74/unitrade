package com.example.unitrade;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.unitrade.databinding.ActivitySigninRegisterBinding;

public class SigninRegisterActivity extends Activity {

    private ActivitySigninRegisterBinding binding;

    private final View.OnClickListener button_signIn_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent newIntent = new Intent(SigninRegisterActivity.this, SignInActivity.class);
            startActivity(newIntent);

        }
    };
    private final View.OnClickListener txt_createAcc_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SigninRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.createAccTxt.setOnClickListener(txt_createAcc_clickListener);
        binding.buttonSignin.setOnClickListener(button_signIn_clickListener);
    }
}