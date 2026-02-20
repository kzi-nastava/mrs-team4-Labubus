package com.example.ubre.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ubre.R;
import com.example.ubre.ui.services.GeocodingService;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

class MapLayerController {

    private final MainActivity activity;
    private final BooleanSupplier isPickOnMapActive;
    private final Consumer<Boolean> setPickOnMapActive;
    private final BooleanSupplier isRideOrderSheetHidden;
    private final Runnable collapseRideOrderSheet;
    private final Supplier<RideOrderAddWaypointController> rideOrderAddWaypointControllerSupplier;
    private final int permissionRequestCode;

    private MapView mapView;
    private MapUiController mapUiController;
    private GeocodingService geocodingService;

    MapLayerController(
            MainActivity activity,
            BooleanSupplier isPickOnMapActive,
            Consumer<Boolean> setPickOnMapActive,
            BooleanSupplier isRideOrderSheetHidden,
            Runnable collapseRideOrderSheet,
            Supplier<RideOrderAddWaypointController> rideOrderAddWaypointControllerSupplier,
            int permissionRequestCode
    ) {
        this.activity = activity;
        this.isPickOnMapActive = isPickOnMapActive;
        this.setPickOnMapActive = setPickOnMapActive;
        this.isRideOrderSheetHidden = isRideOrderSheetHidden;
        this.collapseRideOrderSheet = collapseRideOrderSheet;
        this.rideOrderAddWaypointControllerSupplier = rideOrderAddWaypointControllerSupplier;
        this.permissionRequestCode = permissionRequestCode;
    }

    void init() {
        mapView = activity.findViewById(R.id.map);
        if (mapView == null) {
            return;
        }
        mapUiController = new MapUiController(activity, mapView);
        mapUiController.init(new MapUiController.OnMapTapListener() {
            @Override
            public void onSingleTap(GeoPoint point) {
                if (!isPickOnMapActive.getAsBoolean() && isRideOrderSheetHidden.getAsBoolean()) {
                    return;
                }
                RideOrderAddWaypointController addWaypointController = rideOrderAddWaypointControllerSupplier.get();
                if (addWaypointController != null) {
                    addWaypointController.addFromMap(point);
                }
                collapseRideOrderSheet.run();
                setPickOnMapActive.accept(false);
            }

            @Override
            public void onLongPress(GeoPoint point) {
            }
        });
        mapUiController.setupLocationOverlay(hasLocationPermission(), () -> ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                permissionRequestCode
        ));
        geocodingService = GeocodingService.getInstance(activity);
    }

    boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != permissionRequestCode) {
            return false;
        }
        boolean granted = false;
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                granted = true;
                break;
            }
        }
        if (granted) {
            if (mapUiController != null) {
                mapUiController.setupLocationOverlay(true, null);
            }
        } else {
            Toast.makeText(activity, "Location permission is required to show your position.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    MapView getMapView() {
        return mapView;
    }

    MapUiController getMapUiController() {
        return mapUiController;
    }

    GeocodingService getGeocodingService() {
        return geocodingService;
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
