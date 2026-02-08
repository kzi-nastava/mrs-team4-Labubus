package com.example.ubre.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.storages.UserStorage;
import com.google.android.material.textfield.TextInputEditText;

public class VehicleInformationFragment extends Fragment {

    private static final String ARG_VEHICLE = "arg_vehicle";

    public static VehicleInformationFragment newInstance() {
        VehicleInformationFragment f = new VehicleInformationFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    public VehicleInformationFragment() { super(R.layout.vehicle_information); }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        TextInputEditText etModel = view.findViewById(R.id.et_vehicle_model);
        TextInputEditText etType = view.findViewById(R.id.et_vehicle_type);
        TextInputEditText etPlates = view.findViewById(R.id.et_license_plates);
        TextInputEditText etSeats = view.findViewById(R.id.et_seats);
        TextInputEditText etBaby = view.findViewById(R.id.et_baby_friendly);
        TextInputEditText etPet = view.findViewById(R.id.et_pet_friendly);

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // vehicle is in user storage
        if (UserStorage.getInstance().getCurrentUserVehicle().getValue() == null) {
            etModel.setText("");
            etType.setText("");
            etPlates.setText("");
            etSeats.setText("");
            etBaby.setText("");
            etPet.setText("");
            return;
        }

        etModel.setText(s(UserStorage.getInstance().getCurrentUserVehicle().getValue().getModel()));
        etType.setText(s(UserStorage.getInstance().getCurrentUserVehicle().getValue().getType().name()));
        etPlates.setText(s(UserStorage.getInstance().getCurrentUserVehicle().getValue().getPlates()));
        etSeats.setText(String.valueOf(UserStorage.getInstance().getCurrentUserVehicle().getValue().getSeats()));
        etBaby.setText(UserStorage.getInstance().getCurrentUserVehicle().getValue().isBabyFriendly() ? "Yes" : "No");
        etPet.setText(UserStorage.getInstance().getCurrentUserVehicle().getValue().isPetFriendly() ? "Yes" : "No");
    }

    private String s(String v) { return v == null ? "" : v; }
}
