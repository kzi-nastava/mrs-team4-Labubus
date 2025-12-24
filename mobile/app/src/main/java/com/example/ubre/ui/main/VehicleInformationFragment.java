package com.example.ubre.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ubre.R;
import com.example.ubre.ui.model.VehicleDto;
import com.google.android.material.textfield.TextInputEditText;

public class VehicleInformationFragment extends Fragment {

    private static final String ARG_VEHICLE = "arg_vehicle";

    public static VehicleInformationFragment newInstance(VehicleDto vehicle) {
        VehicleInformationFragment f = new VehicleInformationFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_VEHICLE, vehicle);
        f.setArguments(b);
        return f;
    }

    public VehicleInformationFragment() { super(R.layout.vehicle_information); }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        VehicleDto vehicle = null;
        if (getArguments() != null) {
            vehicle = (VehicleDto) getArguments().getSerializable(ARG_VEHICLE);
        }

        TextInputEditText etModel = view.findViewById(R.id.et_vehicle_model);
        TextInputEditText etType = view.findViewById(R.id.et_vehicle_type);
        TextInputEditText etPlates = view.findViewById(R.id.et_license_plates);
        TextInputEditText etSeats = view.findViewById(R.id.et_seats);
        TextInputEditText etBaby = view.findViewById(R.id.et_baby_friendly);
        TextInputEditText etPet = view.findViewById(R.id.et_pet_friendly);

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        if (vehicle == null) return;

        etModel.setText(s(vehicle.getModel()));
        etType.setText(s(vehicle.getType()));
        etPlates.setText(s(vehicle.getPlates()));
        etSeats.setText(String.valueOf(vehicle.getSeats()));
        etBaby.setText(vehicle.isBabyFriendly() ? "Yes" : "No");
        etPet.setText(vehicle.isPetFriendly() ? "Yes" : "No");
    }

    private String s(String v) { return v == null ? "" : v; }
}
