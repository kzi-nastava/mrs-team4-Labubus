package com.example.ubre.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ubre.R;
import com.example.ubre.ui.main.MainActivity;

public class LoginSignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnGuest = findViewById(R.id.btnGuest);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginSignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginSignupActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        btnGuest.setOnClickListener(v -> {
            Intent intent = new Intent(LoginSignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}