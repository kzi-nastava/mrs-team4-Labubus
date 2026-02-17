package com.example.ubre.ui.main;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.enums.VehicleType;
import com.google.android.material.textfield.TextInputEditText;

import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

class RideOrderCoordinator {

    private final MainActivity activity;
    private final RideOrderUiController rideOrderUiController;
    private final RideOrderSheetController rideOrderSheetController;
    private final RideOrderLogicController rideOrderLogicController;
    private final RideOrderAddWaypointController rideOrderAddWaypointController;
    private final AutocompleteController autocompleteController;
    private final TextInputEditText rideOrderFromInput;
    private final TextInputEditText rideOrderToInput;
    private final View rideOptionStandard;
    private final View rideOptionLuxury;
    private final View rideOptionVan;
    private final View rideOptionBabyFriendly;
    private final View rideOptionPetFriendly;
    private final View btnMapSearch;
    private final DrawerLayout drawer;
    private final IntUnaryOperator dpToPx;
    private final Supplier<Role> currentRoleSupplier;
    private final Consumer<Boolean> setPickOnMapActive;
    private final Runnable collapseRideOrderSheet;
    private final Runnable hideRideOrderSheet;

    private VehicleType selectedVehicleType = VehicleType.STANDARD;
    private boolean bound = false;

    RideOrderCoordinator(
            MainActivity activity,
            RideOrderUiController rideOrderUiController,
            RideOrderSheetController rideOrderSheetController,
            RideOrderLogicController rideOrderLogicController,
            RideOrderAddWaypointController rideOrderAddWaypointController,
            AutocompleteController autocompleteController,
            TextInputEditText rideOrderFromInput,
            TextInputEditText rideOrderToInput,
            View rideOptionStandard,
            View rideOptionLuxury,
            View rideOptionVan,
            View rideOptionBabyFriendly,
            View rideOptionPetFriendly,
            View btnMapSearch,
            DrawerLayout drawer,
            IntUnaryOperator dpToPx,
            Supplier<Role> currentRoleSupplier,
            Consumer<Boolean> setPickOnMapActive,
            Runnable collapseRideOrderSheet,
            Runnable hideRideOrderSheet
    ) {
        this.activity = activity;
        this.rideOrderUiController = rideOrderUiController;
        this.rideOrderSheetController = rideOrderSheetController;
        this.rideOrderLogicController = rideOrderLogicController;
        this.rideOrderAddWaypointController = rideOrderAddWaypointController;
        this.autocompleteController = autocompleteController;
        this.rideOrderFromInput = rideOrderFromInput;
        this.rideOrderToInput = rideOrderToInput;
        this.rideOptionStandard = rideOptionStandard;
        this.rideOptionLuxury = rideOptionLuxury;
        this.rideOptionVan = rideOptionVan;
        this.rideOptionBabyFriendly = rideOptionBabyFriendly;
        this.rideOptionPetFriendly = rideOptionPetFriendly;
        this.btnMapSearch = btnMapSearch;
        this.drawer = drawer;
        this.dpToPx = dpToPx;
        this.currentRoleSupplier = currentRoleSupplier;
        this.setPickOnMapActive = setPickOnMapActive;
        this.collapseRideOrderSheet = collapseRideOrderSheet;
        this.hideRideOrderSheet = hideRideOrderSheet;
    }

    void bind() {
        if (bound) {
            return;
        }
        bound = true;
        rideOrderUiController.bindCallbacks(new RideOrderUiController.Callbacks() {
            @Override
            public void onUseMyLocation() {
                if (rideOrderAddWaypointController != null) {
                    rideOrderAddWaypointController.addFromMyLocation();
                }
            }

            @Override
            public void onPickOnMap() {
                setPickOnMapActive.accept(true);
                collapseRideOrderSheet.run();
                Toast.makeText(activity, "Tap on the map to add a waypoint.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToggleCollapsed() {
                rideOrderSheetController.toggleCollapsed();
            }

            @Override
            public void onSelectOption(View option) {
                selectRideOption(option);
            }

            @Override
            public void onToggleExtra(View option) {
                toggleExtraOption(option);
            }

            @Override
            public void onConfirm() {
                rideOrderLogicController.submitRideOrder(
                        currentRoleSupplier.get(),
                        selectedVehicleType,
                        rideOptionBabyFriendly.isSelected(),
                        rideOptionPetFriendly.isSelected()
                );
            }

            @Override
            public boolean onFromEditorAction(int actionId) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (autocompleteController != null) {
                        autocompleteController.geocodeFromInput(rideOrderFromInput, true);
                        autocompleteController.hideFromSuggestions();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onToEditorAction(int actionId) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (autocompleteController != null) {
                        autocompleteController.geocodeFromInput(rideOrderToInput, false);
                        autocompleteController.hideToSuggestions();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onFromFocusLost() {
                if (autocompleteController != null) {
                    autocompleteController.hideFromSuggestions();
                }
            }

            @Override
            public void onToFocusLost() {
                if (autocompleteController != null) {
                    autocompleteController.hideToSuggestions();
                }
            }
        });

        selectRideOption(rideOptionStandard);
        rideOrderLogicController.initRidePriceCard();
        rideOrderLogicController.initScheduleTimeInputs(TimeFieldWatcher::new);
        rideOrderLogicController.addInviteEmailRow();

        btnMapSearch.setOnClickListener(v -> rideOrderSheetController.toggle());
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                hideRideOrderSheet.run();
            }
        });
    }

    VehicleType getSelectedVehicleType() {
        return selectedVehicleType;
    }

    private void selectRideOption(View selected) {
        rideOptionStandard.setSelected(selected == rideOptionStandard);
        rideOptionLuxury.setSelected(selected == rideOptionLuxury);
        rideOptionVan.setSelected(selected == rideOptionVan);
        applyRideOptionScale(rideOptionStandard, selected == rideOptionStandard);
        applyRideOptionScale(rideOptionLuxury, selected == rideOptionLuxury);
        applyRideOptionScale(rideOptionVan, selected == rideOptionVan);
        if (selected == rideOptionStandard) {
            selectedVehicleType = VehicleType.STANDARD;
        } else if (selected == rideOptionVan) {
            selectedVehicleType = VehicleType.VAN;
        } else if (selected == rideOptionLuxury) {
            selectedVehicleType = VehicleType.LUXURY;
        }
        rideOrderLogicController.requestPriceEstimate(selectedVehicleType);
    }

    private void toggleExtraOption(View option) {
        option.setSelected(!option.isSelected());
        applyRideOptionScale(option, option.isSelected());
    }

    private void applyRideOptionScale(View option, boolean selected) {
        option.animate()
                .scaleX(selected ? 1.01f : 1.0f)
                .scaleY(selected ? 1.01f : 1.0f)
                .setDuration(120)
                .start();
        option.setElevation(dpToPx.applyAsInt(3));
    }

    private static class TimeFieldWatcher implements TextWatcher {
        private final int min;
        private final int max;
        private final View nextFocus;

        TimeFieldWatcher(int min, int max, View nextFocus) {
            this.min = min;
            this.max = max;
            this.nextFocus = nextFocus;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s == null) {
                return;
            }
            String text = s.toString();
            if (text.length() == 2 && nextFocus != null) {
                nextFocus.requestFocus();
            }
            if (text.isEmpty()) {
                return;
            }
            try {
                int value = Integer.parseInt(text);
                if (value < min || value > max) {
                    s.replace(0, s.length(), String.format("%02d", min));
                }
            } catch (NumberFormatException e) {
                s.clear();
            }
        }
    }
}
