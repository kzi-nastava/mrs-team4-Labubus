package com.example.ubre.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.ubre.R;
import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.LoginApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        EditText etEmail = view.findViewById(R.id.etResetEmail);
        Button btnSend = view.findViewById(R.id.btnSendResetLink);
        LoginApi loginApi = ApiClient.getClient().create(LoginApi.class);


        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();


            if (!email.isEmpty()) {

                loginApi.forgotPassword(email).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.e("Forgot Password", "Success: " + response.code());
                            Toast.makeText(getContext(), "Reset link sent to: " + email, Toast.LENGTH_LONG).show();
                            dismiss();
                        } else {
                            Log.e("Forgot Password", "Failed: " + response.code());
                            Toast.makeText(v.getContext(), "Error trying to forget password.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e("Forgot Password", "Failed");
                        Toast.makeText(v.getContext(), "Error trying to forget password.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                etEmail.setError("Please enter your email");
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}