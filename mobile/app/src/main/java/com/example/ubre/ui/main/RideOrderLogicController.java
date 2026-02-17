package com.example.ubre.ui.main;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.RideOrderRequest;
import com.example.ubre.ui.dtos.RideOrderWaypoint;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.enums.VehicleType;
import com.example.ubre.ui.services.PriceEstimateService;
import com.example.ubre.ui.services.RidePlanningService;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.services.RouteService;
import com.example.ubre.ui.storages.RidePlanningStorage;
import com.example.ubre.ui.storages.UserStorage;
import com.example.ubre.ui.utils.TopToast;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import org.osmdroid.views.MapView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RideOrderLogicController {
    private final Activity activity;
    private final FrameLayout priceContainer;
    private final MaterialCheckBox scheduleCheck;
    private final TextInputEditText hourInput;
    private final TextInputEditText minuteInput;
    private final LinearLayout inviteContainer;
    private final MapView mapView;
    private final MapUiController mapUiController;

    private TextView priceValue;

    private Double lastRouteDistanceMeters;
    private Double lastRouteDurationSeconds;
    private Double lastPriceEstimate;
    private int priceRequestSeq = 0;
    private Double lastPriceDistanceMeters;
    private Integer lastPriceVehicleType;

    public RideOrderLogicController(Activity activity,
                                    FrameLayout priceContainer,
                                    MaterialCheckBox scheduleCheck,
                                    TextInputEditText hourInput,
                                    TextInputEditText minuteInput,
                                    LinearLayout inviteContainer,
                                    MapView mapView,
                                    MapUiController mapUiController) {
        this.activity = activity;
        this.priceContainer = priceContainer;
        this.scheduleCheck = scheduleCheck;
        this.hourInput = hourInput;
        this.minuteInput = minuteInput;
        this.inviteContainer = inviteContainer;
        this.mapView = mapView;
        this.mapUiController = mapUiController;
    }

    public void initScheduleTimeInputs(TextWatcherFactory watcherFactory) {
        if (hourInput == null || minuteInput == null) {
            return;
        }
        LocalTime now = LocalTime.now();
        hourInput.setText(String.format("%02d", now.getHour()));
        minuteInput.setText(String.format("%02d", now.getMinute()));

        hourInput.addTextChangedListener(watcherFactory.create(0, 23, minuteInput));
        minuteInput.addTextChangedListener(watcherFactory.create(0, 59, null));
    }

    public void initRidePriceCard() {
        if (priceContainer == null) {
            return;
        }
        priceContainer.removeAllViews();
        View card = activity.getLayoutInflater().inflate(R.layout.stat_card, priceContainer, false);

        TextView tvValue = card.findViewById(R.id.stat_value);
        TextView tvLabel = card.findViewById(R.id.stat_label);

        tvValue.setText("--");
        tvLabel.setText("Estimated price");

        priceContainer.addView(card);
        priceValue = tvValue;
    }

    public void resetRouteAndPriceOnWaypointsChanged() {
        lastRouteDistanceMeters = null;
        lastRouteDurationSeconds = null;
        lastPriceEstimate = null;
        updatePriceEstimate(null);
    }

    public void onRouteInfo(double meters, double durationSeconds, VehicleType selectedVehicleType) {
        lastRouteDistanceMeters = meters;
        lastRouteDurationSeconds = durationSeconds;
        requestPriceEstimate(selectedVehicleType);
    }

    public void onRouteCleared() {
        lastRouteDistanceMeters = null;
        lastRouteDurationSeconds = null;
        updatePriceEstimate(null);
    }

    public void requestPriceEstimate(VehicleType selectedVehicleType) {
        if (lastRouteDistanceMeters == null) {
            updatePriceEstimate(null);
            return;
        }
        int vehicleTypeValue = mapVehicleType(selectedVehicleType);
        priceRequestSeq++;
        int requestId = priceRequestSeq;
        lastPriceDistanceMeters = lastRouteDistanceMeters;
        lastPriceVehicleType = vehicleTypeValue;
        lastPriceEstimate = null;
        updatePriceEstimate(null);

        PriceEstimateService.getInstance(activity).estimate(lastRouteDistanceMeters, vehicleTypeValue, new PriceEstimateService.PriceEstimateCallback() {
            @Override
            public void onResult(Double price) {
                activity.runOnUiThread(() -> {
                    if (requestId != priceRequestSeq) return;
                    if (lastRouteDistanceMeters == null || lastPriceDistanceMeters == null) return;
                    if (Math.abs(lastRouteDistanceMeters - lastPriceDistanceMeters) > 0.5) return;
                    if (lastPriceVehicleType == null || lastPriceVehicleType != mapVehicleType(selectedVehicleType)) return;
                    updatePriceEstimate(price);
                });
            }

            @Override
            public void onError(Throwable t) {
                activity.runOnUiThread(() -> {
                    if (requestId != priceRequestSeq) return;
                    updatePriceEstimate(null);
                });
            }
        });
    }

    public void submitRideOrder(Role currentRole,
                                VehicleType selectedVehicleType,
                                boolean babyFriendly,
                                boolean petFriendly) {
        if (currentRole == Role.GUEST) {
            TopToast.show(activity, "Guest limit", "Guests cannot place orders.");
            return;
        }
        List<WaypointDto> waypoints = RidePlanningStorage.getInstance().getWaypointsSnapshot();
        if (waypoints.size() < 2) {
            TopToast.show(activity, "Order error", "Please select at least 2 waypoints.");
            return;
        }
        if (lastRouteDistanceMeters == null || lastRouteDurationSeconds == null) {
            TopToast.show(activity, "Order error", "Route info is not ready yet.");
            return;
        }
        if (lastPriceEstimate == null) {
            TopToast.show(activity, "Order error", "Price estimate is not ready yet.");
            return;
        }

        Long creatorId = UserStorage.getInstance().getCurrentUser().getValue() != null
                ? UserStorage.getInstance().getCurrentUser().getValue().getId()
                : null;
        if (creatorId == null) {
            TopToast.show(activity, "Order error", "User not authenticated.");
            return;
        }

        List<String> passengers = collectPassengerEmails();
        List<RideOrderWaypoint> orderWaypoints = new ArrayList<>();
        for (WaypointDto wp : waypoints) {
            orderWaypoints.add(new RideOrderWaypoint(
                    0L,
                    wp.getLabel(),
                    wp.getLatitude(),
                    wp.getLongitude(),
                    false
            ));
        }

        String scheduledTime = buildScheduledTimeOrEmpty();
        if (!scheduledTime.isEmpty() && !validateScheduledTimeWindow(scheduledTime)) {
            return;
        }
        int vehicleTypeValue = mapVehicleType(selectedVehicleType);

        RideOrderRequest request = new RideOrderRequest(
                0L,
                creatorId,
                passengers,
                orderWaypoints,
                vehicleTypeValue,
                babyFriendly,
                petFriendly,
                scheduledTime,
                lastRouteDistanceMeters,
                lastRouteDurationSeconds,
                lastPriceEstimate
        );

        RideService.getInstance().orderRide(activity, request, new RideService.OrderCallback() {
            @Override
            public void onSuccess(RideDto ride) {
                clearRidePlanningState();
                TopToast.show(activity, "Ride order", "Ride ordered successfully.");
            }

            @Override
            public void onError(String message) {
                TopToast.show(activity, "Order error", message);
            }
        });
    }

    public void clearRidePlanningState() {
        RidePlanningService.getInstance().clear();
        if (inviteContainer != null) {
            inviteContainer.removeAllViews();
            addInviteEmailRow();
        }
        lastRouteDistanceMeters = null;
        lastRouteDurationSeconds = null;
        lastPriceEstimate = null;
        updatePriceEstimate(null);
        RouteService.getInstance().clearRoute(mapView);
        if (mapUiController != null) {
            mapUiController.clearRideOrderMarkers();
        }
    }

    public void addInviteEmailRow() {
        if (inviteContainer == null) {
            return;
        }
        View row = activity.getLayoutInflater().inflate(R.layout.invite_passenger_item, inviteContainer, false);
        View remove = row.findViewById(R.id.invite_remove);
        TextInputEditText input = row.findViewById(R.id.invite_email_input);

        remove.setOnClickListener(v -> {
            if (inviteContainer.getChildCount() <= 1) {
                input.setText("");
                input.requestFocus();
                return;
            }
            inviteContainer.removeView(row);
        });

        int index = inviteContainer.getChildCount() + 1;
        input.setAutofillHints("invite_email_" + index);
        input.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
                    || actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                addInviteEmailRow();
                return true;
            }
            return false;
        });

        inviteContainer.addView(row);
        input.requestFocus();
    }

    private void updatePriceEstimate(Double price) {
        if (priceValue == null) {
            return;
        }
        if (price == null) {
            priceValue.setText("--");
            lastPriceEstimate = null;
        } else {
            priceValue.setText("$" + String.format("%.2f", price));
            lastPriceEstimate = price;
        }
    }

    private int mapVehicleType(VehicleType type) {
        switch (type) {
            case STANDARD:
                return 0;
            case LUXURY:
                return 1;
            case VAN:
                return 2;
            default:
                return 0;
        }
    }

    private boolean validateScheduledTimeWindow(String scheduledTime) {
        try {
            LocalDateTime scheduled = LocalDateTime.parse(scheduledTime);
            LocalDateTime now = LocalDateTime.now();
            if (scheduled.isBefore(now)) {
                TopToast.show(activity, "Order error", "Scheduled time cannot be in the past.");
                return false;
            }
            long minutesAhead = Duration.between(now, scheduled).toMinutes();
            if (minutesAhead > 300) {
                TopToast.show(activity, "Order error", "Scheduled time must be within 5 hours.");
                return false;
            }
            return true;
        } catch (Exception e) {
            TopToast.show(activity, "Order error", "Invalid scheduled time.");
            return false;
        }
    }

    private List<String> collectPassengerEmails() {
        List<String> emails = new ArrayList<>();
        if (inviteContainer == null) {
            return emails;
        }
        for (int i = 0; i < inviteContainer.getChildCount(); i++) {
            View row = inviteContainer.getChildAt(i);
            TextInputEditText input = row.findViewById(R.id.invite_email_input);
            if (input != null && input.getText() != null) {
                String value = input.getText().toString().trim();
                if (!value.isEmpty()) {
                    emails.add(value);
                }
            }
        }
        return emails;
    }

    private String buildScheduledTimeOrEmpty() {
        if (scheduleCheck == null || !scheduleCheck.isChecked()) {
            return "";
        }
        if (hourInput == null || minuteInput == null
                || hourInput.getText() == null || minuteInput.getText() == null) {
            return "";
        }
        String hh = hourInput.getText().toString().trim();
        String mm = minuteInput.getText().toString().trim();
        if (hh.isEmpty() || mm.isEmpty()) {
            return "";
        }
        int hour = Integer.parseInt(hh);
        int minute = Integer.parseInt(mm);
        java.time.LocalDate today = java.time.LocalDate.now();
        return String.format("%sT%02d:%02d:00", today, hour, minute);
    }

    public interface TextWatcherFactory {
        android.text.TextWatcher create(int min, int max, View nextFocus);
    }
}
