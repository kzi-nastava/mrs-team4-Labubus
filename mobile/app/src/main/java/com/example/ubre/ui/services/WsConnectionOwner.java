package com.example.ubre.ui.services;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.VehicleIndicatorDto;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.enums.RideStatus;
import com.example.ubre.ui.notifications.VehicleLocationNotification;
import com.example.ubre.ui.services.UserService;
import com.example.ubre.ui.dtos.PanicNotificationDto;
import com.example.ubre.ui.enums.NotificationType;
import com.example.ubre.ui.notifications.ProfileChangeNotification;
import com.example.ubre.ui.notifications.RideAssignmentNotification;
import com.example.ubre.ui.notifications.CurrentRideNotification;
import com.example.ubre.ui.notifications.RideReminderNotification;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.RideDetailsStorage;
import com.example.ubre.ui.storages.UserStorage;
import com.example.ubre.ui.storages.VehicleLocationStorage;
import com.example.ubre.ui.utils.NotificationHelper;
import com.example.ubre.ui.utils.TopToast;
import com.example.ubre.ui.storages.CurrentRideStorage;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.List;

public class WsConnectionOwner {
    private static final String TAG = "WS";
    private static WsConnectionOwner instance;

    private final Context appContext;
    private final Handler mainHandler;
    private final Gson gson;
    private StompWebSocketManager wsManager;
    private Long currentUserId;
    private boolean subscriptionsRegistered = false;
    private boolean connectIssued = false;
    private boolean vehicleLocationSubscribed = false;
    private long lastRequestAtMs = 0L;
    private static final long DEBOUNCE_MS = 1000L;


    private WsConnectionOwner(Context context) {
        this.appContext = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.gson = new Gson();
    }

    public static synchronized WsConnectionOwner getInstance(Context context) {
        if (instance == null) {
            instance = new WsConnectionOwner(context);
        }
        return instance;
    }

    public synchronized void connectPublic() {
        Log.i(TAG, "connectPublic requested");
        if (wsManager == null) {
            String wsUrl = StompWebSocketManager.buildSockJsWebSocketUrl(
                    ApiClient.SERVICE_API_PATH, "/ws");
            wsManager = new StompWebSocketManager(appContext, wsUrl);
            wsManager.setStateListener(state -> {
                if (state == StompWebSocketManager.State.CONNECTED) {
                    connectIssued = false;
                }
            });
        }
        if (!connectIssued && currentUserId == null && !subscriptionsRegistered) {
            wsManager.connect();
            connectIssued = true;
        }
        subscribeVehicleLocations();
    }

    public synchronized void requestConnectForUser(Long userId, String source) {
        Log.i(TAG, "connect requested from " + source + " userId=" + userId);
        if (userId == null || userId == 0L) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastRequestAtMs < DEBOUNCE_MS) {
            Log.i(TAG, "connect request debounced");
            return;
        }
        lastRequestAtMs = now;

        if (wsManager == null) {
            String wsUrl = StompWebSocketManager.buildSockJsWebSocketUrl(
                    ApiClient.SERVICE_API_PATH,
                    "/ws"
            );
            wsManager = new StompWebSocketManager(appContext, wsUrl);
            wsManager.setStateListener(state -> {
                if (state == StompWebSocketManager.State.CONNECTED) {
                    connectIssued = false;
                }
            });
        }

        if (currentUserId != null && currentUserId.equals(userId)) {
            if (subscriptionsRegistered || connectIssued) {
                return;
            }
        } else {
            if (wsManager != null) {
                wsManager.unsubscribeAll();
                wsManager.disconnect();
            }
            subscriptionsRegistered = false;
            vehicleLocationSubscribed = false;
            connectIssued = false;
        }

        currentUserId = userId;
        wsManager.connect();
        connectIssued = true;
        if (!subscriptionsRegistered) {
            registerUserSubscriptions(userId);
            subscriptionsRegistered = true;
        }
    }

    public synchronized void stop(String source) {
        Log.i(TAG, "disconnect requested from " + source);
        if (wsManager != null) {
            wsManager.unsubscribeAll();
            wsManager.disconnect();
        }
        currentUserId = null;
        subscriptionsRegistered = false;
        vehicleLocationSubscribed = false;
        connectIssued = false;
        lastRequestAtMs = 0L;
        connectPublic();
    }

    private void subscribeVehicleLocations() {
        if (wsManager == null || vehicleLocationSubscribed) {
            return;
        }
        vehicleLocationSubscribed = true;
        wsManager.subscribe("/topic/vehicle-locations", (topic, payload) -> {
            handleVehicleLocationNotification(payload);
        });
    }

    private void registerUserSubscriptions(Long userId) {
        if (wsManager == null) {
            return;
        }
        subscribeVehicleLocations();
        wsManager.subscribe("/topic/profile-changes/" + userId, (topic, payload) -> {
            Log.i(TAG, "msg " + topic + " " + payload);
            handleProfileChangeNotification(payload);
        });
        wsManager.subscribe("/topic/ride-assignments/" + userId, (topic, payload) -> {
            Log.i(TAG, "msg " + topic + " " + payload);
            handleRideAssignmentNotification(payload);
        });
        wsManager.subscribe("/topic/ride-reminders/" + userId, (topic, payload) -> {
            Log.i(TAG, "msg " + topic + " " + payload);
            handleRideReminderNotification(payload);
        });
        wsManager.subscribe("/topic/current-rides/" + userId, (topic, payload) -> { // HOLY F************!!!!!!!!!!!!! singlar and plural!!!!!!!!!!!!!!!!!
            Log.i(TAG, "msg " + topic + " " + payload);
            handleCurrentRideNotification(payload);
        });
        SharedPreferences prefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String role = prefs.getString("role", "");

        if (role.equals("ADMIN")) {
            wsManager.subscribe("/topic/panic", (topic, payload) -> {
                handlePanicNotification(payload);
            });
        }
    }


    private void handleProfileChangeNotification(String payload) {
        ProfileChangeNotification notification;
        try {
            notification = gson.fromJson(payload, ProfileChangeNotification.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse profile change notification", e);
            return;
        }
        if (notification == null || notification.getStatus() == null) {
            return;
        }
        if (notification.getStatus() == NotificationType.PROFILE_CHANGE_APPROVED) {
            onProfileChangeApproved();
        } else if (notification.getStatus() == NotificationType.PROFILE_CHANGE_REJECTED) {
            onProfileChangeRejected();
        }
    }

    private void onProfileChangeApproved() {
        mainHandler.post(() -> {
            TopToast.show(appContext, "Profile update", "Your profile change was approved.");
            try {
                UserService.getInstance(appContext).loadCurrentUser();
            } catch (Exception e) {
                Log.e(TAG, "Failed to refresh current user after profile change", e);
            }
        });
    }

    private void onProfileChangeRejected() {
        mainHandler.post(() ->
                TopToast.show(appContext, "Profile update", "Your profile change was rejected.")
        );
    }

    private void handleRideAssignmentNotification(String payload) {
        RideAssignmentNotification notification;
        try {
            notification = gson.fromJson(payload, RideAssignmentNotification.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse ride assignment notification", e);
            return;
        }
        if (notification == null || notification.getStatus() == null) {
            return;
        }
        if (notification.getStatus() == NotificationType.RIDE_ASSIGNED) {
            onRideAssigned();
        }
    }

    private void onRideAssigned() {
        mainHandler.post(() ->
                TopToast.show(appContext, "New ride", "A ride has been assigned to you.")
        );
    }

    private void handleRideReminderNotification(String payload) {
        RideReminderNotification notification;
        try {
            notification = gson.fromJson(payload, RideReminderNotification.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse ride reminder notification", e);
            return;
        }
        if (notification == null || notification.getStatus() == null) {
            return;
        }
        if (notification.getStatus() == NotificationType.RIDE_REMINDER) {
            onRideReminder(notification.getTime());
        }
    }

    private void onRideReminder(String time) {
        String safeTime = time == null || time.trim().isEmpty() ? "soon" : time.trim();
        mainHandler.post(() ->
                TopToast.show(appContext, "Ride reminder", "You have a ride scheduled at " + safeTime + ".")
        );
    }

    private void handleCurrentRideNotification(String payload) {
        CurrentRideNotification notification;
        try {
            notification = gson.fromJson(payload, CurrentRideNotification.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse current ride notification", e);
            return;
        }
        if (notification == null || notification.getStatus() == null) {
            return;
        }
        if (notification.getStatus() == NotificationType.TIME_FOR_A_RIDE) {
            onTimeForRide(notification);
        } else if (notification.getStatus() == NotificationType.RIDE_STARTED) {
            onRideStarted(notification);
        } else if (notification.getStatus() == NotificationType.RIDE_COMPLETED) {
            onRideCompleted();
        }
    }

    private void onTimeForRide(CurrentRideNotification notification) {
        mainHandler.post(() -> {
            TopToast.show(appContext, "Get ready", "Your ride is starting soon...");
            if (notification != null && notification.getRide() != null) {
                CurrentRideStorage.getInstance().setCurrentRide(notification.getRide());
            }
        });
    }

    private void onRideStarted(CurrentRideNotification notification) {
        mainHandler.post(() -> {
            TopToast.show(appContext, "Ride started", "Your ride has been started successfully.");
            if (notification != null && notification.getRide() != null) {
                RideDto ride = notification.getRide();
                ride.setStatus(RideStatus.IN_PROGRESS);
                CurrentRideStorage.getInstance().setCurrentRide(ride);
            }
        });
    }

    private void onRideCompleted() {
        mainHandler.post(() -> {
            TopToast.show(appContext, "Ride completed", "Your ride has been completed.");
            UserDto user = UserStorage.getInstance().getCurrentUser().getValue();
            RideDto ride = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue();
            if (user != null && ride != null && ride.getCreatedBy().equals(user.getId()))
                ReviewStorage.getInstance().setRideId(ride.getId());

            CurrentRideStorage.getInstance().clear();
        });
    }

    private void handleVehicleLocationNotification(String payload) {
        VehicleLocationNotification notification;
        try {
            notification = gson.fromJson(payload, VehicleLocationNotification.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse vehicle location notification", e);
            return;
        }
        if (notification == null || notification.getIndicators() == null) {
            return;
        }

        List<VehicleIndicatorDto> indicators = notification.getIndicators();

        RideDto currentRide = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue();
        if (currentRide != null && currentRide.getStatus() == RideStatus.IN_PROGRESS
                && currentRide.getDriver() != null && currentRide.getWaypoints() != null) {
            Long driverId = currentRide.getDriver().getId();
            VehicleIndicatorDto driverIndicator = null;
            for (VehicleIndicatorDto ind : indicators) {
                if (ind.getDriverId() != null && ind.getDriverId().equals(driverId)) {
                    driverIndicator = ind;
                    break;
                }
            }
            if (driverIndicator != null && driverIndicator.getLocation() != null
                    && driverIndicator.getLocation().getLatitude() != null
                    && driverIndicator.getLocation().getLongitude() != null) {
                double vLat = driverIndicator.getLocation().getLatitude();
                double vLon = driverIndicator.getLocation().getLongitude();
                boolean anyNewlyVisited = false;
                for (WaypointDto wp : currentRide.getWaypoints()) {
                    if (wp.getVisited() != null && wp.getVisited()) continue;
                    if (wp.getLatitude() == null || wp.getLongitude() == null) continue;
                    double dLat = Math.abs(wp.getLatitude() - vLat);
                    double dLon = Math.abs(wp.getLongitude() - vLon);
                    if (dLat < 0.00030 && dLon < 0.00054) {
                        wp.setVisited(true);
                        anyNewlyVisited = true;
                    }
                }
                if (anyNewlyVisited) {
                    mainHandler.post(() ->
                            CurrentRideStorage.getInstance().setCurrentRide(currentRide)
                    );
                }
            }
        }

        mainHandler.post(() -> {VehicleLocationStorage.getInstance().setLocations(indicators);});
    }

    private void handlePanicNotification(String payload) {
        PanicNotificationDto notification;
        try {
            notification = gson.fromJson(payload, PanicNotificationDto.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse ride assignment notification", e);
            return;
        }
        mainHandler.post(() -> NotificationHelper.showPanic(appContext, "Panic activated", "Ride #" + notification.getRideId()));
    }
}
