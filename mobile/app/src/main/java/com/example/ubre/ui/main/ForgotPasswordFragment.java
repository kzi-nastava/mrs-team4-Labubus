package com.example.ubre.ui.main;

import android.os.Bundle;
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

public class ForgotPasswordFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        EditText etEmail = view.findViewById(R.id.etResetEmail);
        Button btnSend = view.findViewById(R.id.btnSendResetLink);

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (!email.isEmpty()) {
                Toast.makeText(getContext(), "Reset link sent to: " + email, Toast.LENGTH_LONG).show();
                dismiss();
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