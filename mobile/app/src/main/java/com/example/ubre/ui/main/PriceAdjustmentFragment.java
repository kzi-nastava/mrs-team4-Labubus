package com.example.ubre.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.VehicleTypePricingDto;
import com.example.ubre.ui.services.PricingService;
import com.google.android.material.textfield.TextInputEditText;

public class PriceAdjustmentFragment extends Fragment {

    private TextInputEditText etPricePerKm;
    private TextInputEditText etStandard;
    private TextInputEditText etLuxury;
    private TextInputEditText etVan;

    private VehicleTypePricingDto originalPricing;

    public static PriceAdjustmentFragment newInstance() {
        return new PriceAdjustmentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_price_adjustment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        etPricePerKm = view.findViewById(R.id.et_price_per_km);
        etStandard = view.findViewById(R.id.et_standard);
        etLuxury = view.findViewById(R.id.et_luxury);
        etVan = view.findViewById(R.id.et_van);

        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        view.findViewById(R.id.btn_save).setOnClickListener(v -> savePricing());
        view.findViewById(R.id.btn_discard).setOnClickListener(v -> discardChanges());

        loadPricing();
    }

    private void loadPricing() {
        PricingService.getInstance(requireContext()).getPricing(new PricingService.PricingCallback() {
            @Override
            public void onSuccess(VehicleTypePricingDto pricing) {
                if (!isAdded()) return;
                originalPricing = pricing;
                requireActivity().runOnUiThread(() -> populateFields(pricing));
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void populateFields(VehicleTypePricingDto pricing) {
        etPricePerKm.setText(String.valueOf(pricing.getPricePerKm()));
        etStandard.setText(String.valueOf(pricing.getStandardBasePrice()));
        etLuxury.setText(String.valueOf(pricing.getLuxuryBasePrice()));
        etVan.setText(String.valueOf(pricing.getVanBasePrice()));
    }

    private void savePricing() {
        VehicleTypePricingDto dto = new VehicleTypePricingDto();
        try {
            dto.setPricePerKm(Double.parseDouble(etPricePerKm.getText().toString()));
            dto.setStandardBasePrice(Double.parseDouble(etStandard.getText().toString()));
            dto.setLuxuryBasePrice(Double.parseDouble(etLuxury.getText().toString()));
            dto.setVanBasePrice(Double.parseDouble(etVan.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        PricingService.getInstance(requireContext()).updatePricing(dto, new PricingService.PricingCallback() {
            @Override
            public void onSuccess(VehicleTypePricingDto pricing) {
                if (!isAdded()) return;
                originalPricing = pricing;
                requireActivity().runOnUiThread(() -> {
                    populateFields(pricing);
                    Toast.makeText(requireContext(), "Pricing updated successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void discardChanges() {
        if (originalPricing != null) {
            populateFields(originalPricing);
        }
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
