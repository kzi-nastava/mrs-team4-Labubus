package com.example.ubre.ui.services;

import android.content.Context;
import android.util.Log;

import com.example.ubre.ui.apis.ApiClient;

public class WsConnectionOwner {
    private static final String TAG = "WS";
    private static WsConnectionOwner instance;

    private final Context appContext;
    private StompWebSocketManager wsManager;
    private Long currentUserId;
    private boolean subscriptionsRegistered = false;
    private boolean connectIssued = false;
    private long lastRequestAtMs = 0L;
    private static final long DEBOUNCE_MS = 1000L;

    private WsConnectionOwner(Context context) {
        this.appContext = context.getApplicationContext();
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
        wsManager.subscribe("/topic/profile-changes/" + userId, (topic, payload) -> Log.i(TAG, "msg " + topic + " " + payload));
        wsManager.subscribe("/topic/ride-assignments/" + userId, (topic, payload) -> Log.i(TAG, "msg " + topic + " " + payload));
        wsManager.subscribe("/topic/ride-reminders/" + userId, (topic, payload) -> Log.i(TAG, "msg " + topic + " " + payload));
        wsManager.subscribe("/topic/current-ride/" + userId, (topic, payload) -> Log.i(TAG, "msg " + topic + " " + payload));
        wsManager.subscribe("/topic/panic", (topic, payload) -> Log.i(TAG, "msg " + topic + " " + payload));
    }
}
