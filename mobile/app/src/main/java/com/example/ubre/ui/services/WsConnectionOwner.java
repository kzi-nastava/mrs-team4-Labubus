package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.dtos.PanicNotificationDto;
import com.example.ubre.ui.enums.NotificationType;
import com.example.ubre.ui.notifications.ProfileChangeNotification;
import com.example.ubre.ui.notifications.RideAssignmentNotification;
import com.example.ubre.ui.notifications.CurrentRideNotification;
import com.example.ubre.ui.notifications.RideReminderNotification;
import com.example.ubre.ui.utils.TopToast;
import com.example.ubre.ui.storages.CurrentRideStorage;
import com.google.gson.Gson;

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
        connectIssued = false;
        lastRequestAtMs = 0L;
    }

    private void registerUserSubscriptions(Long userId) {
        if (wsManager == null) {
            return;
        }
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
                Log.i(TAG, "msg " + topic + " " + payload);
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
            onRideStarted();
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

    private void onRideStarted() {
        mainHandler.post(() ->
                TopToast.show(appContext, "Ride started", "Your ride has been started successfully.")
        );
    }

    private void handlePanicNotification(String payload) {
        PanicNotificationDto notification;
        try {
            notification = gson.fromJson(payload, PanicNotificationDto.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse ride assignment notification", e);
            return;
        }
        mainHandler.post(() ->
                TopToast.show(appContext, "Panic activated", "Ride id: " + notification.getRideId().toString())
        );
    }
}
