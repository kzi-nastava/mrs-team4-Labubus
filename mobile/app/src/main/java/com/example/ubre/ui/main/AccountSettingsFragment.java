package com.example.ubre.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ubre.R;
import com.example.ubre.ui.model.Role;
import com.example.ubre.ui.model.UserDto;
import com.example.ubre.ui.model.VehicleDto;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AccountSettingsFragment extends Fragment {

    private static final String ARG_USER = "arg_user";
    private static final String ARG_VEHICLE = "arg_vehicle";

    public static AccountSettingsFragment newInstance(UserDto user, VehicleDto vehicle) {
        AccountSettingsFragment f = new AccountSettingsFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_USER, user); // ako UserDto nije Serializable, reci
        b.putSerializable(ARG_VEHICLE, vehicle);
        f.setArguments(b);
        return f;
    }

    public AccountSettingsFragment() { super(); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        UserDto user = (args != null) ? (UserDto) args.getSerializable(ARG_USER) : null;

        boolean isDriver = user != null && user.getRole() != null && user.getRole() == Role.DRIVER;

        int layout = isDriver ? R.layout.account_settings_driver : R.layout.account_settings;
        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        TextInputLayout tilName = view.findViewById(R.id.til_name);
        TextInputLayout tilSurname = view.findViewById(R.id.til_surname);
        TextInputLayout tilPhone = view.findViewById(R.id.til_phone);

        TextInputEditText etEmail = view.findViewById(R.id.et_email);
        TextInputEditText etName = view.findViewById(R.id.et_name);
        TextInputEditText etSurname = view.findViewById(R.id.et_surname);
        TextInputEditText etAddress = view.findViewById(R.id.et_address);
        TextInputEditText etPhone = view.findViewById(R.id.et_phone);

        ImageView avatar = view.findViewById(R.id.img_avatar);

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // PREFILL iz DTO
        UserDto user = null;
        if (getArguments() != null) user = (UserDto) getArguments().getSerializable(ARG_USER);

        if (user != null) {
            etEmail.setText(user.getEmail());
            etName.setText(user.getName());
            etSurname.setText(user.getSurname());
            etAddress.setText(user.getAddress());
            etPhone.setText(user.getPhone());

            String url = user.getAvatarUrl();
            if (url != null && !url.isEmpty()) {
                Glide.with(this).load(url).circleCrop().into(avatar);
            } else {
                Glide.with(this).load(R.drawable.img_default_avatar).circleCrop().into(avatar);
            }
        }

        // Change password -> fragment
        view.findViewById(R.id.btn_change_password).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ChangePasswordFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // View vehicle inforamtion -> fragment (only for drivers)
        Bundle args = getArguments();

        final VehicleDto vehicle = (VehicleDto) (args != null ? args.getSerializable(ARG_VEHICLE) : null);

        View btnVehicle = view.findViewById(R.id.btn_view_vehicle_information);

        if (btnVehicle != null) {
            if (vehicle != null) {
                btnVehicle.setOnClickListener(v ->
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, VehicleInformationFragment.newInstance(vehicle))
                                .addToBackStack(null)
                                .commit()
                );
            } else {
                btnVehicle.setVisibility(View.GONE);
            }
        }

        // Clear error čim krene da kuca
        clearErrorOnType(etName, tilName);
        clearErrorOnType(etSurname, tilSurname);
        clearErrorOnType(etPhone, tilPhone);

        view.findViewById(R.id.btn_save).setOnClickListener(v -> {
            boolean ok = true;

            if (isEmpty(etName)) { tilName.setError("Required"); ok = false; } else tilName.setError(null);
            if (isEmpty(etSurname)) { tilSurname.setError("Required"); ok = false; } else tilSurname.setError(null);
            if (isEmpty(etPhone)) { tilPhone.setError("Required"); ok = false; } else tilPhone.setError(null);

            if (!ok) return;

            // TODO: paziv API / repo i upis u DTO
            // user.setName(etName.getText().toString().trim()); ...
        });

        view.findViewById(R.id.btn_discard).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private boolean isEmpty(TextInputEditText et) {
        return et.getText() == null || et.getText().toString().trim().isEmpty();
    }

    private void clearErrorOnType(TextInputEditText et, TextInputLayout til) {
        et.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (s != null && !s.toString().trim().isEmpty()) til.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }
}
