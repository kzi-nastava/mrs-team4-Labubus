package com.example.ubre.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.PasswordChangeDto;
import com.example.ubre.ui.services.AccountSettingsService;
import com.example.ubre.ui.storages.UserStorage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordFragment extends Fragment {

    private static final String TAG = "ChangePasswordFragment";

    public ChangePasswordFragment() { super(R.layout.change_password); }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        TextInputLayout tilNew = view.findViewById(R.id.til_new);
        TextInputLayout tilConfirm = view.findViewById(R.id.til_confirm);
        TextInputEditText etNew = view.findViewById(R.id.et_new);
        TextInputEditText etConfirm = view.findViewById(R.id.et_confirm);
        TextView txtError = view.findViewById(R.id.txt_error);

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        view.findViewById(R.id.btn_discard).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        view.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String p1 = etNew.getText() == null ? "" : etNew.getText().toString().trim();
            String p2 = etConfirm.getText() == null ? "" : etConfirm.getText().toString().trim();

            boolean ok = true;

            if (p1.isEmpty()) { tilNew.setError("Required"); ok = false; } else tilNew.setError(null);
            if (p2.isEmpty()) { tilConfirm.setError("Required"); ok = false; } else tilConfirm.setError(null);

            if (!ok) {
                txtError.setVisibility(View.GONE);
                return;
            }

            if (ok && !p1.equals(p2)) {
                // crveno + tekst
                tilConfirm.setError(" ");
                txtError.setVisibility(View.VISIBLE);
                return;
            }

            txtError.setVisibility(View.GONE);
            tilNew.setError(null);
            tilConfirm.setError(null);

            // api call for changing password goes here
            PasswordChangeDto passwordChangeDto = new PasswordChangeDto();
            passwordChangeDto.setNewPassword(p1);

            // extract user id from current context (shared pref or user storage)
            Long userId = UserStorage.getInstance().getCurrentUser().getValue().getId();
            passwordChangeDto.setUserId(userId);

            try {
                Context context = requireContext().getApplicationContext(); // crash if there is no context
                AccountSettingsService.getInstance(context).changePassword(passwordChangeDto);
            } catch (Exception e) {
                Log.e(TAG, "Save password failed", e);
            }

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // čim korisnik kuca, skloni greške
        etConfirm.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                txtError.setVisibility(View.GONE);
                tilConfirm.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }
}
