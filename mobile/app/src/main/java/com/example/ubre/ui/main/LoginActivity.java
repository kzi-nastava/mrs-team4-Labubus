package com.example.ubre.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ubre.R;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        ImageView btnBack = findViewById(R.id.btn_back);
        Button btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        Button btnContinueGuest = findViewById(R.id.btnContinueGuest);
        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(etEmail.getText());
                String password = String.valueOf(etPassword.getText());

                if (email.isEmpty()) {
                    etEmail.setError("Email is required.");
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password is required.");
                    return;
                }
            }
        });


        btnContinueGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            ForgotPasswordFragment fragment = new ForgotPasswordFragment();
            fragment.show(getSupportFragmentManager(), "ForgotPasswordDialog");
        });
    }
}