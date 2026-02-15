package com.example.ubre.ui.main;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ubre.R;
import com.example.ubre.ui.enums.VehicleType;
import com.example.ubre.ui.utils.TopToast;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class RegisterDriverFragment extends Fragment {

    public static RegisterDriverFragment newInstance() {
        return new RegisterDriverFragment();
    }

    private Uri selectedAvatarUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.register_driver, container, false);

        v.findViewById(R.id.btn_back).setOnClickListener(x ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        v.findViewById(R.id.btn_cancel).setOnClickListener(x ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        setupAvatarPicker(v);
        setupVehicleTypeDropdown(v);
        setupSeatStepper(v);
        setupValidation(v);

        return v;
    }

    private void setupAvatarPicker(View v) {
        ImageView avatar = v.findViewById(R.id.img_driver_photo);

        ActivityResultLauncher<String> pickImage =
                registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        selectedAvatarUri = uri;
                        Glide.with(this)
                                .load(uri)
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(avatar);
                    }
                });

        avatar.setOnClickListener(x -> pickImage.launch("image/*"));
    }

    private void setupVehicleTypeDropdown(View v) {
        MaterialAutoCompleteTextView dropdown = v.findViewById(R.id.et_vehicle_type);
        if (dropdown == null) return;

        String[] items = new String[VehicleType.values().length];
        int i = 0;
        for (VehicleType type : VehicleType.values()) {
            items[i++] = formatVehicleType(type);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_item,
                items
        );

        dropdown.setAdapter(adapter);
        dropdown.setDropDownBackgroundResource(R.drawable.bg_dropdown_menu);
    }

    private String formatVehicleType(VehicleType type) {
        String raw = type.name().toLowerCase(Locale.ROOT);
        return raw.substring(0, 1).toUpperCase(Locale.ROOT) + raw.substring(1);
    }

    private void setupSeatStepper(View v) {
        final int minSeats = 2;
        final int maxSeats = 9;

        TextInputEditText etSeats = v.findViewById(R.id.et_seats);

        if (etSeats != null && etSeats.getText() != null && etSeats.getText().length() == 0) {
            etSeats.setText(String.valueOf(minSeats));
        }

        v.findViewById(R.id.btn_seats_minus).setOnClickListener(x -> {
            int current = parseSeats(etSeats, minSeats);
            if (current > minSeats) {
                etSeats.setText(String.valueOf(current - 1));
            }
        });

        v.findViewById(R.id.btn_seats_plus).setOnClickListener(x -> {
            int current = parseSeats(etSeats, minSeats);
            if (current < maxSeats) {
                etSeats.setText(String.valueOf(current + 1));
            }
        });
    }

    private int parseSeats(com.google.android.material.textfield.TextInputEditText etSeats,
                           int fallback) {
        if (etSeats == null || etSeats.getText() == null) return fallback;
        try {
            String value = etSeats.getText().toString().trim();
            if (value.isEmpty()) return fallback;
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private void setupValidation(View v) {
        TextInputLayout tilEmail = v.findViewById(R.id.til_email);
        TextInputLayout tilPassword = v.findViewById(R.id.til_password);
        TextInputLayout tilConfirmPassword = v.findViewById(R.id.til_confirm_password);
        TextInputLayout tilName = v.findViewById(R.id.til_name);
        TextInputLayout tilSurname = v.findViewById(R.id.til_surname);
        TextInputLayout tilAddress = v.findViewById(R.id.til_address);
        TextInputLayout tilPhone = v.findViewById(R.id.til_phone);
        TextInputLayout tilVehicleModel = v.findViewById(R.id.til_vehicle_model);
        TextInputLayout tilVehicleType = v.findViewById(R.id.til_vehicle_type);
        TextInputLayout tilLicensePlates = v.findViewById(R.id.til_license_plates);
        TextInputLayout tilSeats = v.findViewById(R.id.til_seats);

        TextInputEditText etEmail = v.findViewById(R.id.et_email);
        TextInputEditText etPassword = v.findViewById(R.id.et_password);
        TextInputEditText etConfirmPassword = v.findViewById(R.id.et_confirm_password);
        TextInputEditText etName = v.findViewById(R.id.et_name);
        TextInputEditText etSurname = v.findViewById(R.id.et_surname);
        TextInputEditText etAddress = v.findViewById(R.id.et_address);
        TextInputEditText etPhone = v.findViewById(R.id.et_phone);
        TextInputEditText etVehicleModel = v.findViewById(R.id.et_vehicle_model);
        MaterialAutoCompleteTextView etVehicleType = v.findViewById(R.id.et_vehicle_type);
        TextInputEditText etLicensePlates = v.findViewById(R.id.et_license_plates);
        TextInputEditText etSeats = v.findViewById(R.id.et_seats);

        clearErrorOnType(etEmail, tilEmail);
        clearErrorOnType(etPassword, tilPassword);
        clearErrorOnType(etConfirmPassword, tilConfirmPassword);
        clearErrorOnType(etName, tilName);
        clearErrorOnType(etSurname, tilSurname);
        clearErrorOnType(etAddress, tilAddress);
        clearErrorOnType(etPhone, tilPhone);
        clearErrorOnType(etVehicleModel, tilVehicleModel);
        clearErrorOnType(etVehicleType, tilVehicleType);
        clearErrorOnType(etLicensePlates, tilLicensePlates);
        clearErrorOnType(etSeats, tilSeats);

        v.findViewById(R.id.btn_register_driver).setOnClickListener(x -> {
            boolean ok = true;

            if (isEmpty(etEmail)) { tilEmail.setError("Required"); ok = false; }
            else if (!isValidEmail(etEmail.getText().toString())) { tilEmail.setError("Invalid email"); ok = false; }
            else tilEmail.setError(null);

            if (isEmpty(etPassword)) { tilPassword.setError("Required"); ok = false; } else tilPassword.setError(null);
            if (!isEmpty(etPassword) && etPassword.getText().toString().length() < 6) {
                tilPassword.setError("Minimum 6 characters");
                ok = false;
            }
            if (isEmpty(etConfirmPassword)) { tilConfirmPassword.setError("Required"); ok = false; } else tilConfirmPassword.setError(null);
            if (isEmpty(etName)) { tilName.setError("Required"); ok = false; } else tilName.setError(null);
            if (isEmpty(etSurname)) { tilSurname.setError("Required"); ok = false; } else tilSurname.setError(null);
            if (isEmpty(etAddress)) { tilAddress.setError("Required"); ok = false; } else tilAddress.setError(null);
            if (isEmpty(etPhone)) { tilPhone.setError("Required"); ok = false; } else tilPhone.setError(null);
            if (isEmpty(etVehicleModel)) { tilVehicleModel.setError("Required"); ok = false; } else tilVehicleModel.setError(null);
            if (isEmpty(etVehicleType)) { tilVehicleType.setError("Required"); ok = false; } else tilVehicleType.setError(null);
            if (isEmpty(etLicensePlates)) { tilLicensePlates.setError("Required"); ok = false; } else tilLicensePlates.setError(null);
            if (isEmpty(etSeats)) { tilSeats.setError("Required"); ok = false; }
            else {
                int seats = parseSeats(etSeats, -1);
                if (seats < 2 || seats > 9) {
                    tilSeats.setError("Seats must be 2-9");
                    ok = false;
                } else {
                    tilSeats.setError(null);
                }
            }

            if (!isEmpty(etPassword) && !isEmpty(etConfirmPassword)) {
                String p1 = etPassword.getText().toString();
                String p2 = etConfirmPassword.getText().toString();
                if (!p1.equals(p2)) {
                    tilConfirmPassword.setError(getString(R.string.password_mismatch_error));
                    ok = false;
                } else {
                    tilConfirmPassword.setError(null);
                }
            }

            if (!ok) {
                TopToast.show(v.getContext(), "Form error", "Please fix highlighted fields.");
                return;
            }

            // TODO: Hook up service call for registration.
        });
    }

    private boolean isEmpty(android.widget.TextView et) {
        return et == null || et.getText() == null || et.getText().toString().trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void clearErrorOnType(android.widget.TextView et, TextInputLayout til) {
        if (et == null || til == null) return;
        et.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (s != null && !s.toString().trim().isEmpty()) til.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

}
