package com.example.ubre.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.example.ubre.R;
import com.example.ubre.ui.dtos.LoginDto;
import com.example.ubre.ui.dtos.LoginTokenDto;
import com.example.ubre.ui.services.LoginService;
import com.example.ubre.ui.services.ServiceUtils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText email, password;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email = findViewById(R.id.etLoginEmail);
        password = findViewById(R.id.etLoginPassword);
        ImageView btnBack = findViewById(R.id.btn_back);
        Button btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        Button btnContinueGuest = findViewById(R.id.btnContinueGuest);
        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        LoginService loginService = ServiceUtils.loginService;


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailText = String.valueOf(email.getText());
                String passwordText = String.valueOf(password.getText());


                if (emailText.isEmpty()) {
                    email.setError("Email is required.");
                    return;
                }

                if (passwordText.isEmpty()) {
                    password.setError("Password is required.");
                    return;
                }

                LoginDto loginDTO = new LoginDto();
                loginDTO.setEmail(emailText);
                loginDTO.setPassword(passwordText);

                loginService.login(loginDTO).enqueue(new Callback<LoginTokenDto>() {

                    public void onResponse(@NonNull Call<LoginTokenDto> call, @NonNull Response<LoginTokenDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            setPreferences(
                                    response.body().getAccessToken(),
                                    response.body().getExpiresIn()
                            );

                            // Load homepage by user_role ?
                            String role = sharedPreferences.getString("role", "");
                            Log.d("Login", role);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.d("Login", "Message received: " + response.code());
                            Toast.makeText(v.getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onFailure(Call<LoginTokenDto> call, Throwable t) {
                        Log.e("Login", "Network error", t);
                    }
                });


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

    private void setPreferences(String accessToken, Long expiresIn) {
        sharedPreferences = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JWT jwt = new JWT(accessToken);
        editor.putString("jwt", accessToken);
        editor.putString("email", jwt.getSubject());
        editor.putString("id", jwt.getClaim("id").asString());
        editor.putString("role", jwt.getClaim("roles").asString());
        editor.apply();
    }

}