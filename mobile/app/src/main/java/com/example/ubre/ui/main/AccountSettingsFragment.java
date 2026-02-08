package com.example.ubre.ui.main;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ubre.ui.dtos.ProfileChangeDto;
import com.example.ubre.ui.dtos.StatItemDto;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ubre.R;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.enums.ProfileChangeStatus;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.dtos.UserStatsDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.services.AccountSettingsService;
import com.example.ubre.ui.storages.UserStorage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AccountSettingsFragment extends Fragment {

    private static final String TAG = "AccountSettingsFragment";
    private static final String ARG_USER = "arg_user";
    private static final String ARG_VEHICLE = "arg_vehicle";

    public static AccountSettingsFragment newInstance(VehicleDto vehicle) {
        AccountSettingsFragment f = new AccountSettingsFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_VEHICLE, vehicle);
        f.setArguments(b);
        return f;
    }

    public AccountSettingsFragment() { super(); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_settings_driver, container, false);
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

        ActivityResultLauncher<String> pickImage =
                registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        Glide.with(this)
                                .load(uri)
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(avatar); // preview only, not saved
                        UserStorage.getInstance().setPendingAvatarUri(uri);
                    }
                });

        avatar.setOnClickListener(v -> pickImage.launch("image/*"));


        LinearLayout statsContainer = view.findViewById(R.id.stats_container);
        View btnVehicle = view.findViewById(R.id.btn_view_vehicle_information);

        Bundle args = getArguments();
        final VehicleDto vehicle = (VehicleDto) (args != null ? args.getSerializable(ARG_VEHICLE) : null);

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // User -> popuni polja + prikaži/sakrij driver stvari
        UserStorage.getInstance().getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;

            etEmail.setText(user.getEmail());
            etName.setText(user.getName());
            etSurname.setText(user.getSurname());
            etAddress.setText(user.getAddress());
            etPhone.setText(user.getPhone());

            boolean isDriver = user.getRole() == Role.DRIVER;

            // STATS
            if (statsContainer != null) {
                statsContainer.setVisibility(isDriver ? View.VISIBLE : View.GONE);

                if (isDriver) {
                    UserStatsDto stats = new UserStatsDto(0L, 0, 0, 0.0, 0.0, 0.0);
                    stats.setActivePast24Hours(450);
                    stats.setDistanceTraveled(1920.0);
                    renderStats(statsContainer, stats);
                } else {
                    // we must hide stats title section as well
                    View statsHeader = view.findViewById(R.id.stats_header);
                    if (statsHeader != null) statsHeader.setVisibility(View.GONE);
                }
            }

            // VEHICLE BUTTON
            if (btnVehicle != null) {
                if (isDriver && vehicle != null) {
                    btnVehicle.setVisibility(View.VISIBLE);
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
        });

        // Avatar
        UserStorage.getInstance().getCurrentUserAvatar().observe(getViewLifecycleOwner(), avatarBytes -> {
            if (avatarBytes != null && avatarBytes.length > 0) {
                Glide.with(this).asBitmap().load(avatarBytes).circleCrop().into(avatar);
            } else {
                Glide.with(this).load(R.drawable.img_default_avatar).circleCrop().into(avatar);
            }
        });

        // Change password
        view.findViewById(R.id.btn_change_password).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ChangePasswordFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // Clear error čim krene da kuca
        clearErrorOnType(etName, tilName);
        clearErrorOnType(etSurname, tilSurname);
        clearErrorOnType(etPhone, tilPhone);

        // Save
        view.findViewById(R.id.btn_save).setOnClickListener(v -> {
            boolean ok = true;

            if (isEmpty(etName)) { tilName.setError("Required"); ok = false; } else tilName.setError(null);
            if (isEmpty(etSurname)) { tilSurname.setError("Required"); ok = false; } else tilSurname.setError(null);
            if (isEmpty(etPhone)) { tilPhone.setError("Required"); ok = false; } else tilPhone.setError(null);

            if (!ok) return;

            // update local storage user
            UserDto old = UserStorage.getInstance().getCurrentUser().getValue();
            if (old == null) return;

            UserDto updated = new UserDto(old);
            updated.setName(etName.getText().toString().trim());
            updated.setSurname(etSurname.getText().toString().trim());
            updated.setPhone(etPhone.getText().toString().trim());
            updated.setAddress(etAddress.getText() != null ? etAddress.getText().toString().trim() : "");

            // api call to update profile goes here
            // UserStorage.getInstance().setCurrentUser(updated);

            // if user role is driver, no we just request profile change (extract role from current user in storage)
            if (UserStorage.getInstance().getCurrentUser().getValue().getRole() == Role.DRIVER) {
                // if there is new avatar, we must create new avater url by merging email + _ + file name
                // NOTE: this part is very risky and make sure to fix later, may be very problematic actually
                Uri pendingAvatarUri = UserStorage.getInstance().getPendingAvatarUri().getValue();
                String newAvatarUrl = UserStorage.getInstance().getCurrentUser().getValue().getAvatarUrl();
                if (pendingAvatarUri != null) {
                    String email = UserStorage.getInstance().getCurrentUser().getValue().getEmail();
                    newAvatarUrl = email + "_avatar.jpg";
                }

                ProfileChangeDto change = new ProfileChangeDto(0L, old.getId(), old.getName(), updated.getName(), old.getSurname(), updated.getSurname(),
                        old.getAddress(), updated.getAddress(), old.getPhone(), updated.getPhone(),
                        old.getAvatarUrl(), newAvatarUrl, ProfileChangeStatus.PENDING);

                // send request to backend
                try {
                    Context context = requireContext().getApplicationContext(); // crash if there is no context
                    AccountSettingsService.getInstance(context).requestProfileChange(change);
                    // approve avatar change immediately without need for admin approval
                    if (pendingAvatarUri != null) {
                        AccountSettingsService.getInstance(context).updateUserAvatar();
                        UserStorage.getInstance().clearPendingAvatarUri();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to request profile change", e);
                }oog
            } else { // for admins, and regular users, we just update profile directly without approval
                UserStorage.getInstance().setCurrentUser(updated);

                try {
                    Context context = requireContext().getApplicationContext(); // crash if there is no context
                    AccountSettingsService.getInstance(context).saveProfileChanges();

                    Uri pending = UserStorage.getInstance().getPendingAvatarUri().getValue();
                    if (pending != null) {
                        AccountSettingsService.getInstance(context).updateUserAvatar();
                        UserStorage.getInstance().clearPendingAvatarUri();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to save profile changes", e);
                }
            }
        });

        view.findViewById(R.id.btn_discard).setOnClickListener(v -> {
            UserStorage.getInstance().clearPendingAvatarUri();
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }


    private void renderStats(LinearLayout container, UserStatsDto stats) {
        container.removeAllViews();

        List<StatItemDto> items = new ArrayList<>();
        items.add(new StatItemDto(formatMinutes(stats.getActivePast24Hours()), "Active in last 24h"));

        LayoutInflater inflater = LayoutInflater.from(container.getContext());

        for (StatItemDto item : items) {
            View card = inflater.inflate(R.layout.stat_card, container, false);

            TextView tvValue = card.findViewById(R.id.stat_value);
            TextView tvLabel = card.findViewById(R.id.stat_label);

            tvValue.setText(item.getValue());
            tvLabel.setText(item.getLabel());

            container.addView(card);
        }
    }

    private String formatMinutes(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        if (h > 0 && m > 0) return h + "h " + m + "m";
        if (h > 0) return h + "h";
        return m + "m";
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
