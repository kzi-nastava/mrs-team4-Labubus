package com.example.ubre.ui.main;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.FrameLayout;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.services.RouteService;
import com.example.ubre.ui.services.WsConnectionOwner;
import com.example.ubre.ui.storages.CurrentRideStorage;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.RidePlanningStorage;
import com.example.ubre.ui.storages.UserStorage;

import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

class MainObserversBinder {

    private final MainActivity activity;
    private final RideOrderWaypointsController rideOrderWaypointsController;
    private final RideOrderLogicController rideOrderLogicController;
    private final MapUiController mapUiController;
    private final MapView mapView;
    private final LoadingIndicatorController loadingIndicatorController;
    private final RideOrderCoordinator rideOrderCoordinator;
    private final MainDrawerController drawerController;
    private boolean bound = false;
    private Long lastReviewRideId = null;

    MainObserversBinder(
            MainActivity activity,
            RideOrderWaypointsController rideOrderWaypointsController,
            RideOrderLogicController rideOrderLogicController,
            MapUiController mapUiController,
            MapView mapView,
            LoadingIndicatorController loadingIndicatorController,
            RideOrderCoordinator rideOrderCoordinator,
            MainDrawerController drawerController
    ) {
        this.activity = activity;
        this.rideOrderWaypointsController = rideOrderWaypointsController;
        this.rideOrderLogicController = rideOrderLogicController;
        this.mapUiController = mapUiController;
        this.mapView = mapView;
        this.loadingIndicatorController = loadingIndicatorController;
        this.rideOrderCoordinator = rideOrderCoordinator;
        this.drawerController = drawerController;
    }

    void bind() {
        if (bound) {
            return;
        }
        bound = true;
        bindRidePlanning();
        bindRoute();
        bindCurrentRide();
        bindReviewModal();
        bindUser();
    }

    private void bindRidePlanning() {
        RidePlanningStorage.getInstance().getWaypointsReadOnly().observe(activity, waypoints -> {
            if (CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue() != null) {
                return;
            }
            List<WaypointDto> safeWaypoints = waypoints == null ? new ArrayList<>() : new ArrayList<>(waypoints);
            rideOrderWaypointsController.renderWaypoints(safeWaypoints);
            if (mapUiController != null) {
                mapUiController.syncRideOrderMarkers(safeWaypoints);
            }
            rideOrderLogicController.resetRouteAndPriceOnWaypointsChanged();
            if (safeWaypoints.size() >= 2) {
                RouteService.getInstance().drawRoute(mapView, safeWaypoints);
            } else {
                RouteService.getInstance().clearRoute(mapView);
            }
        });
    }

    private void bindCurrentRide() {
        CurrentRideStorage.getInstance().getCurrentRideReadOnly().observe(activity, ride -> {
            activity.setCurrentRideActive(ride != null);
            if (mapUiController == null || mapView == null) {
                return;
            }
            if (ride == null) {
                List<WaypointDto> planning = RidePlanningStorage.getInstance().getWaypointsSnapshot();
                if (planning == null || planning.isEmpty()) {
                    mapUiController.clearRideOrderMarkers();
                    RouteService.getInstance().clearRoute(mapView);
                } else {
                    mapUiController.syncRideOrderMarkers(planning);
                    if (planning.size() >= 2) {
                        RouteService.getInstance().drawRoute(mapView, planning);
                    } else {
                        RouteService.getInstance().clearRoute(mapView);
                    }
                }
                return;
            }

            List<WaypointDto> waypoints = ride.getWaypoints();
            List<WaypointDto> safeWaypoints = waypoints == null ? new ArrayList<>() : new ArrayList<>(waypoints);
            if (safeWaypoints.isEmpty()) {
                mapUiController.clearRideOrderMarkers();
                RouteService.getInstance().clearRoute(mapView);
                return;
            }
            mapUiController.syncRideOrderMarkers(safeWaypoints);
            if (safeWaypoints.size() >= 2) {
                RouteService.getInstance().drawRoute(mapView, safeWaypoints);
            } else {
                RouteService.getInstance().clearRoute(mapView);
            }
        });
    }

    private void bindRoute() {
        RouteService.getInstance().setRouteLoadingListener(isLoading -> {
            if (loadingIndicatorController != null) {
                loadingIndicatorController.onRouteLoadingChanged(isLoading);
            }
        });
        RouteService.getInstance().setRouteInfoListener(new RouteService.RouteInfoListener() {
            @Override
            public void onRouteInfo(double meters, double durationSeconds) {
                if (rideOrderCoordinator != null) {
                    rideOrderLogicController.onRouteInfo(
                            meters,
                            durationSeconds,
                            rideOrderCoordinator.getSelectedVehicleType()
                    );
                }
            }

            @Override
            public void onRouteCleared() {
                rideOrderLogicController.onRouteCleared();
            }
        });
    }

    private void bindReviewModal() {
        ReviewStorage.getInstance().getRideId().observe(activity, rideId -> {
            if (rideId != null) {
                if (lastReviewRideId != null && lastReviewRideId.equals(rideId)) {
                    return;
                }
                lastReviewRideId = rideId;
                activity.showModal(new ReviewModalFragment());
            } else {
                lastReviewRideId = null;
                FrameLayout modalContainer = activity.findViewById(R.id.modal_container);
                modalContainer.setVisibility(View.GONE);
                modalContainer.removeAllViews();
            }
        });
    }

    private void bindUser() {
        UserStorage.getInstance().getCurrentUser().observe(activity, currentUser -> {
            if (drawerController == null) {
                return;
            }
            String token2 = getToken();
            if (currentUser == null) {
                if (token2 != null && !token2.isEmpty()) {
                    return;
                }
                drawerController.setMenuOptions(Role.GUEST);
                WsConnectionOwner.getInstance(activity.getApplicationContext())
                        .stop("MainActivity.currentUser=null");
                return;
            }
            drawerController.setMenuOptions(currentUser.getRole());
            drawerController.fillDrawerHeader();
            Long userId = currentUser.getId();
            if (userId == null || userId == 0L) {
                return;
            }
            WsConnectionOwner.getInstance(activity.getApplicationContext())
                    .requestConnectForUser(userId, "MainActivity.currentUser");
        });

        UserStorage.getInstance().getCurrentUserAvatar().observe(activity, avatar -> {
            if (drawerController != null) {
                drawerController.fillDrawerHeader();
            }
        });

        UserStorage.getInstance().getBlockNote().observe(activity, note -> {
            UserDto currentUser = UserStorage.getInstance().getCurrentUser().getValue();
            boolean isBlocked = currentUser != null && Boolean.TRUE.equals(currentUser.getIsBlocked());
            if (activity != null) {
                activity.updateBlockNotice(note, isBlocked);
            }
        });
    }

    private String getToken() {
        SharedPreferences sp2 = activity.getSharedPreferences("app_prefs", MainActivity.MODE_PRIVATE);
        return sp2.getString("jwt", null);
    }
}
