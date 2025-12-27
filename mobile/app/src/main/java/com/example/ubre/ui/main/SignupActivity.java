package com.example.ubre.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ubre.R;

public class SignupActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etAddress, etPhone, etPassword, etConfirmPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        ImageView btnBack = findViewById(R.id.btn_back);
        TextView btnLogin = findViewById(R.id.tvLogin);

        btnBack.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

    }

}