package com.example.ubre.ui.main;

import android.app.Activity;
import android.widget.Toast;

import com.example.ubre.ui.services.GeocodingService;
import com.example.ubre.ui.services.RidePlanningService;
import com.example.ubre.ui.utils.TopToast;

import org.osmdroid.util.GeoPoint;

public class RideOrderAddWaypointController {
    public interface CanAddWaypoint {
        boolean canAdd();
    }

    public interface LabelFormatter {
        String format(String displayName);
    }

    public interface GeocodeCallbacks {
        void onStart();
        void onEnd();
    }

    private final Activity activity;
    private final MapUiController mapUiController;
    private final GeocodingService geocodingService;
    private final CanAddWaypoint canAddWaypoint;
    private final LabelFormatter labelFormatter;
    private final GeocodeCallbacks geocodeCallbacks;

    public RideOrderAddWaypointController(Activity activity,
                                          MapUiController mapUiController,
                                          GeocodingService geocodingService,
                                          CanAddWaypoint canAddWaypoint,
                                          LabelFormatter labelFormatter,
                                          GeocodeCallbacks geocodeCallbacks) {
        this.activity = activity;
        this.mapUiController = mapUiController;
        this.geocodingService = geocodingService;
        this.canAddWaypoint = canAddWaypoint;
        this.labelFormatter = labelFormatter;
        this.geocodeCallbacks = geocodeCallbacks;
    }

    public void addFromMyLocation() {
        if (!canAddWaypoint.canAdd()) {
            TopToast.show(activity, "Guest limit", "Guests can add up to 2 waypoints.");
            return;
        }
        if (mapUiController == null || mapUiController.getMyLocation() == null) {
            Toast.makeText(activity, "Current location not available yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        GeoPoint point = mapUiController.getMyLocation();
        addWaypoint("My location", point);
    }

    public void addFromMap(GeoPoint point) {
        if (!canAddWaypoint.canAdd()) {
            TopToast.show(activity, "Guest limit", "Guests can add up to 2 waypoints.");
            return;
        }
        addWaypoint("Pinned location", point);
    }

    private void addWaypoint(String baseLabel, GeoPoint point) {
        RidePlanningService.getInstance().addWaypoint(
                new com.example.ubre.ui.dtos.WaypointDto(null, baseLabel, point.getLatitude(), point.getLongitude())
        );
        int index = com.example.ubre.ui.storages.RidePlanningStorage.getInstance()
                .getWaypointsSnapshot().size() - 1;
        if (geocodingService != null) {
            geocodeCallbacks.onStart();
            geocodingService.reverse(point.getLatitude(), point.getLongitude(), new GeocodingService.GeocodingCallback() {
                @Override
                public void onResult(com.example.ubre.ui.dtos.GeocodingResult result) {
                    geocodeCallbacks.onEnd();
                    if (result == null || result.displayName == null) {
                        return;
                    }
                    String label = labelFormatter.format(result.displayName);
                    activity.runOnUiThread(() ->
                            RidePlanningService.getInstance().updateWaypointLabelAt(index, label)
                    );
                }

                @Override
                public void onError(Throwable t) {
                    geocodeCallbacks.onEnd();
                }
            });
        }
    }
}
