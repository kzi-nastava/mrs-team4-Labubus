package com.example.ubre.ui.main;

import android.app.Activity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.AutocompleteAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

public class RideOrderUiController {

    public interface Callbacks {
        void onUseMyLocation();
        void onPickOnMap();
        void onToggleCollapsed();
        void onSelectOption(View option);
        void onToggleExtra(View option);
        void onConfirm();
        boolean onFromEditorAction(int actionId);
        boolean onToEditorAction(int actionId);
        void onFromFocusLost();
        void onToFocusLost();
    }

    public final View rideOrderSheet;
    public final TextInputEditText fromInput;
    public final TextInputEditText toInput;
    public final View useMyLocation;
    public final View pickOnMap;
    public final View stopsContainer;
    public final View handle;
    public final View optionStandard;
    public final View optionLuxury;
    public final View optionVan;
    public final View optionBaby;
    public final View optionPet;
    public final View priceContainer;
    public final MaterialCheckBox scheduleCheck;
    public final View scheduleContainer;
    public final TextInputEditText hourInput;
    public final TextInputEditText minuteInput;
    public final View inviteContainer;
    public final View confirmButton;
    public final RecyclerView fromSuggestionsView;
    public final RecyclerView toSuggestionsView;
    public final View rideOrderOptionsTitle;
    public final View rideOrderInviteTitle;

    public RideOrderUiController(Activity activity) {
        rideOrderSheet = activity.findViewById(R.id.ride_order_sheet);
        fromInput = activity.findViewById(R.id.ride_order_from_input);
        toInput = activity.findViewById(R.id.ride_order_to_input);
        useMyLocation = activity.findViewById(R.id.ride_order_use_my_location);
        pickOnMap = activity.findViewById(R.id.ride_order_pick_on_map);
        stopsContainer = activity.findViewById(R.id.ride_order_stops_container);
        handle = activity.findViewById(R.id.ride_order_handle);
        optionStandard = activity.findViewById(R.id.ride_option_standard);
        optionLuxury = activity.findViewById(R.id.ride_option_luxury);
        optionVan = activity.findViewById(R.id.ride_option_van);
        optionBaby = activity.findViewById(R.id.ride_order_baby_friendly);
        optionPet = activity.findViewById(R.id.ride_order_pet_friendly);
        priceContainer = activity.findViewById(R.id.ride_order_price_container);
        scheduleCheck = activity.findViewById(R.id.ride_order_schedule_check);
        scheduleContainer = activity.findViewById(R.id.ride_order_schedule_container);
        hourInput = activity.findViewById(R.id.ride_order_time_hour_input);
        minuteInput = activity.findViewById(R.id.ride_order_time_minute_input);
        inviteContainer = activity.findViewById(R.id.ride_order_invite_container);
        confirmButton = activity.findViewById(R.id.ride_order_confirm);
        fromSuggestionsView = activity.findViewById(R.id.ride_order_from_suggestions);
        toSuggestionsView = activity.findViewById(R.id.ride_order_to_suggestions);
        rideOrderOptionsTitle = activity.findViewById(R.id.ride_order_options_title);
        rideOrderInviteTitle = activity.findViewById(R.id.ride_order_invite_title);

    }

    public void bindAdapters(AutocompleteAdapter fromAdapter, AutocompleteAdapter toAdapter) {
        fromSuggestionsView.setLayoutManager(new LinearLayoutManager(fromSuggestionsView.getContext()));
        fromSuggestionsView.setAdapter(fromAdapter);
        toSuggestionsView.setLayoutManager(new LinearLayoutManager(toSuggestionsView.getContext()));
        toSuggestionsView.setAdapter(toAdapter);
    }

    public void bindCallbacks(Callbacks callbacks) {
        useMyLocation.setOnClickListener(v -> callbacks.onUseMyLocation());
        pickOnMap.setOnClickListener(v -> callbacks.onPickOnMap());
        handle.setOnClickListener(v -> callbacks.onToggleCollapsed());

        optionStandard.setOnClickListener(v -> callbacks.onSelectOption(optionStandard));
        optionLuxury.setOnClickListener(v -> callbacks.onSelectOption(optionLuxury));
        optionVan.setOnClickListener(v -> callbacks.onSelectOption(optionVan));

        optionBaby.setOnClickListener(v -> callbacks.onToggleExtra(optionBaby));
        optionPet.setOnClickListener(v -> callbacks.onToggleExtra(optionPet));

        scheduleCheck.setOnCheckedChangeListener((buttonView, isChecked) ->
                scheduleContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        confirmButton.setOnClickListener(v -> callbacks.onConfirm());

        fromInput.setOnEditorActionListener((v, actionId, event) -> callbacks.onFromEditorAction(actionId));
        toInput.setOnEditorActionListener((v, actionId, event) -> callbacks.onToEditorAction(actionId));

        fromInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) callbacks.onFromFocusLost();
        });
        toInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) callbacks.onToFocusLost();
        });
    }

    public void updateGuestState(boolean isGuest) {

        int visibility = isGuest ? View.GONE : View.VISIBLE;
        if (optionStandard != null) optionStandard.setVisibility(visibility);
        if (optionLuxury != null) optionLuxury.setVisibility(visibility);
        if (optionVan != null) optionVan.setVisibility(visibility);
        if (optionBaby != null) optionBaby.setVisibility(visibility);
        if (optionPet != null) optionPet.setVisibility(visibility);

        if (scheduleCheck != null) scheduleCheck.setVisibility(visibility);
        if (hourInput != null) hourInput.setVisibility(visibility);
        if (minuteInput != null) minuteInput.setVisibility(visibility);
        if (rideOrderInviteTitle != null) rideOrderInviteTitle.setVisibility(visibility);
        if (rideOrderOptionsTitle != null) rideOrderOptionsTitle.setVisibility(visibility);
        if (scheduleContainer != null) scheduleContainer.setVisibility(visibility);

        if (inviteContainer != null) inviteContainer.setVisibility(visibility);
    }
}
