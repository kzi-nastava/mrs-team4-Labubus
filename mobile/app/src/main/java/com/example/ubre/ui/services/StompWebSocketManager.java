package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class StompWebSocketManager {
    public enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    public interface StateListener {
        void onStateChanged(State state);
    }

    public interface MessageCallback {
        void onMessage(String topic, String payload);
    }

    private static final String TAG = "WS";
    private static final int DEFAULT_QUEUE_LIMIT = 100;
    private static final long RECONNECT_DELAY_MS = 5000L;

    private final Context appContext;
    private final OkHttpClient okHttpClient;
    private final Handler mainHandler;
    private final Gson gson;
    private final String wsUrl;
    private final int queueLimit;

    private final Object lock = new Object();
    private final Map<String, MessageCallback> requestedSubscriptions = new HashMap<>();
    private final Map<String, String> activeSubscriptionIds = new HashMap<>();
    private final Queue<PendingSend> sendQueue = new ArrayDeque<>();

    private final StringBuilder incomingBuffer = new StringBuilder();

    private State state = State.DISCONNECTED;
    private StateListener stateListener;
    private WebSocket webSocket;
    private boolean reconnectScheduled = false;
    private boolean manualDisconnect = false;

    private final Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lock) {
                reconnectScheduled = false;
            }
            connect();
        }
    };

    public StompWebSocketManager(Context context, String wsUrl) {
        this(context, wsUrl, DEFAULT_QUEUE_LIMIT, new Gson());
    }

    public StompWebSocketManager(Context context, String wsUrl, int queueLimit, Gson gson) {
        this.appContext = context.getApplicationContext();
        this.wsUrl = wsUrl;
        this.queueLimit = queueLimit;
        this.gson = gson;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.okHttpClient = new OkHttpClient.Builder()
                .readTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build();
    }

    public void setStateListener(@Nullable StateListener listener) {
        synchronized (lock) {
            this.stateListener = listener;
        }
    }

    public void connect() {
        Log.i(TAG, "connect() called");
        synchronized (lock) {
            if (state == State.CONNECTING || state == State.CONNECTED) {
                Log.d(TAG, "connect() ignored; state=" + state);
                return;
            }
            manualDisconnect = false;
            setStateLocked(State.CONNECTING);
        }

        Log.i(TAG, "WS connect attempt: " + wsUrl);
        Request request = new Request.Builder().url(wsUrl).build();
        webSocket = okHttpClient.newWebSocket(request, new StompListener());
    }

    public void disconnect() {
        synchronized (lock) {
            manualDisconnect = true;
            reconnectScheduled = false;
            mainHandler.removeCallbacks(reconnectRunnable);
        }
        if (webSocket != null) {
            webSocket.close(1000, "Client disconnect");
        }
        cleanupAfterDisconnect();
        setState(State.DISCONNECTED);
    }

    public void subscribe(String topic, MessageCallback callback) {
        synchronized (lock) {
            requestedSubscriptions.put(topic, callback);
        }
        if (state == State.CONNECTED) {
            subscribeInternal(topic);
        }
    }

    public void unsubscribe(String topic) {
        synchronized (lock) {
            requestedSubscriptions.remove(topic);
        }
        if (state == State.CONNECTED) {
            unsubscribeInternal(topic);
        }
    }

    public void unsubscribeAll() {
        Map<String, String> activeCopy;
        synchronized (lock) {
            activeCopy = new HashMap<>(activeSubscriptionIds);
            requestedSubscriptions.clear();
            activeSubscriptionIds.clear();
        }
        for (String id : activeCopy.values()) {
            sendFrame(buildUnsubscribeFrame(id));
        }
    }

    public void send(String destination, String jsonString) {
        enqueueOrSend(destination, jsonString);
    }

    public void send(String destination, Object payload) {
        if (payload == null) {
            enqueueOrSend(destination, "null");
        } else if (payload instanceof String) {
            enqueueOrSend(destination, (String) payload);
        } else {
            enqueueOrSend(destination, gson.toJson(payload));
        }
    }

    private void enqueueOrSend(String destination, String payload) {
        boolean shouldConnect = false;
        synchronized (lock) {
            if (state != State.CONNECTED) {
                if (sendQueue.size() >= queueLimit) {
                    sendQueue.poll();
                }
                sendQueue.offer(new PendingSend(destination, payload));
                Log.i(TAG, "Queued send. size=" + sendQueue.size());
                shouldConnect = state != State.CONNECTING;
            }
        }
        if (shouldConnect) {
            connect();
            return;
        }
        if (state == State.CONNECTED) {
            boolean ok = sendFrame(buildSendFrame(destination, payload));
            if (!ok) {
                synchronized (lock) {
                    if (sendQueue.size() >= queueLimit) {
                        sendQueue.poll();
                    }
                    sendQueue.offer(new PendingSend(destination, payload));
                    Log.i(TAG, "Send failed, re-queued. size=" + sendQueue.size());
                }
            }
        }
    }

    private void subscribeInternal(String topic) {
        synchronized (lock) {
            if (activeSubscriptionIds.containsKey(topic)) {
                return;
            }
            String id = "sub-" + UUID.randomUUID();
            activeSubscriptionIds.put(topic, id);
            sendFrame(buildSubscribeFrame(id, topic));
            Log.i(TAG, "SUBSCRIBE sent: " + topic);
        }
    }

    private void unsubscribeInternal(String topic) {
        String id;
        synchronized (lock) {
            id = activeSubscriptionIds.remove(topic);
        }
        if (id != null) {
            sendFrame(buildUnsubscribeFrame(id));
        }
    }

    private void resubscribeAll() {
        int count = 0;
        synchronized (lock) {
            for (String topic : requestedSubscriptions.keySet()) {
                String id = "sub-" + UUID.randomUUID();
                activeSubscriptionIds.put(topic, id);
                sendFrame(buildSubscribeFrame(id, topic));
                count++;
            }
        }
        Log.i(TAG, "Resubscribed topics count=" + count);
    }

    private void flushSendQueue() {
        while (true) {
            PendingSend item;
            synchronized (lock) {
                item = sendQueue.poll();
                if (item == null) {
                    Log.i(TAG, "Send queue empty.");
                    return;
                }
                Log.i(TAG, "Send queue size now=" + sendQueue.size());
            }
            boolean ok = sendFrame(buildSendFrame(item.destination, item.payload));
            if (!ok) {
                synchronized (lock) {
                    if (sendQueue.size() >= queueLimit) {
                        sendQueue.poll();
                    }
                    sendQueue.offer(item);
                    Log.i(TAG, "Flush send failed, re-queued. size=" + sendQueue.size());
                }
                return;
            }
        }
    }

    private void cleanupAfterDisconnect() {
        synchronized (lock) {
            activeSubscriptionIds.clear();
            incomingBuffer.setLength(0);
        }
    }

    private void scheduleReconnect() {
        synchronized (lock) {
            if (manualDisconnect) {
                return;
            }
            if (reconnectScheduled || state == State.CONNECTING) {
                return;
            }
            reconnectScheduled = true;
        }
        Log.i(TAG, "Scheduling reconnect in " + RECONNECT_DELAY_MS + "ms");
        mainHandler.postDelayed(reconnectRunnable, RECONNECT_DELAY_MS);
    }

    private boolean sendFrame(String frame) {
        WebSocket ws;
        synchronized (lock) {
            ws = webSocket;
        }
        if (ws == null) {
            return false;
        }
        return ws.send(frame);
    }

    private String getToken() {
        SharedPreferences sp = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return sp.getString("jwt", null);
    }

    private String buildConnectFrame() {
        String token = getToken();
        StringBuilder sb = new StringBuilder();
        sb.append("CONNECT\n");
        sb.append("accept-version:1.2\n");
        sb.append("heart-beat:0,0\n");
        if (token != null && !token.isEmpty()) {
            sb.append("Authorization: Bearer ").append(token).append("\n");
        } else {
            Log.w(TAG, "CONNECT without Authorization token");
        }
        sb.append("\n\u0000");
        return sb.toString();
    }

    public static String buildSockJsWebSocketUrl(String baseHttpUrl, String stompPath) {
        String base = baseHttpUrl;
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String path = stompPath;
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        String wsBase;
        if (base.startsWith("https://")) {
            wsBase = "wss://" + base.substring("https://".length());
        } else if (base.startsWith("http://")) {
            wsBase = "ws://" + base.substring("http://".length());
        } else if (base.startsWith("ws://") || base.startsWith("wss://")) {
            wsBase = base;
        } else {
            wsBase = "ws://" + base;
        }
        return wsBase + path + "/websocket";
    }

    private String buildSubscribeFrame(String id, String destination) {
        return "SUBSCRIBE\n" +
                "id:" + id + "\n" +
                "destination:" + destination + "\n\n" +
                "\u0000";
    }

    private String buildUnsubscribeFrame(String id) {
        return "UNSUBSCRIBE\n" +
                "id:" + id + "\n\n" +
                "\u0000";
    }

    private String buildSendFrame(String destination, String payload) {
        byte[] bytes = payload == null ? new byte[0] : payload.getBytes(StandardCharsets.UTF_8);
        return "SEND\n" +
                "destination:" + destination + "\n" +
                "content-type:application/json\n" +
                "content-length:" + bytes.length + "\n\n" +
                (payload == null ? "" : payload) +
                "\u0000";
    }

    private void handleFrame(String frame) {
        int commandEnd = frame.indexOf('\n');
        if (commandEnd <= 0) {
            return;
        }
        String command = frame.substring(0, commandEnd).trim();
        int headersEnd = frame.indexOf("\n\n");
        String headersPart = headersEnd >= 0 ? frame.substring(commandEnd + 1, headersEnd) : "";
        String body = headersEnd >= 0 ? frame.substring(headersEnd + 2) : "";

        Map<String, String> headers = new HashMap<>();
        if (!headersPart.isEmpty()) {
            String[] lines = headersPart.split("\n");
            for (String line : lines) {
                int idx = line.indexOf(':');
                if (idx > 0 && idx < line.length() - 1) {
                    headers.put(line.substring(0, idx), line.substring(idx + 1));
                }
            }
        }

        if ("CONNECTED".equals(command)) {
            Log.i(TAG, "STOMP CONNECTED");
            setState(State.CONNECTED);
            resubscribeAll();
            flushSendQueue();
            return;
        }

        if ("MESSAGE".equals(command)) {
            String topic = headers.get("destination");
            if (topic == null) {
                String subId = headers.get("subscription");
                if (subId != null) {
                    topic = findTopicBySubscriptionId(subId);
                }
            }
            final String finalTopic = topic == null ? "" : topic;
            final String finalBody = body;
            Log.i(TAG, "MESSAGE recv: " + finalTopic + " payload=" + shortenPayload(finalBody, 120));
            MessageCallback callback;
            synchronized (lock) {
                callback = requestedSubscriptions.get(finalTopic);
            }
            if (callback != null) {
                mainHandler.post(() -> callback.onMessage(finalTopic, finalBody));
            }
            return;
        }

        if ("ERROR".equals(command)) {
            Log.i(TAG, "STOMP ERROR: " + body);
            cleanupAfterDisconnect();
            setState(State.DISCONNECTED);
            if (webSocket != null) {
                webSocket.close(1011, "STOMP ERROR");
            }
            scheduleReconnect();
        }
    }

    private String findTopicBySubscriptionId(String subId) {
        synchronized (lock) {
            for (Map.Entry<String, String> entry : activeSubscriptionIds.entrySet()) {
                if (subId.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private void setState(State newState) {
        synchronized (lock) {
            setStateLocked(newState);
        }
    }

    private void setStateLocked(State newState) {
        if (state == newState) {
            return;
        }
        state = newState;
        StateListener listener = stateListener;
        if (listener != null) {
            mainHandler.post(() -> listener.onStateChanged(newState));
        }
    }

    private String shortenPayload(String payload, int maxChars) {
        if (payload == null) {
            return "";
        }
        if (payload.length() <= maxChars) {
            return payload;
        }
        return payload.substring(0, Math.max(0, maxChars - 3)) + "...";
    }

    private class StompListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.i(TAG, "WS OPENED");
            Log.i(TAG, "WS handshake: " + response.code() + " " + response.message());
            boolean sent = webSocket.send(buildConnectFrame());
            Log.i(TAG, "CONNECT frame sent=" + sent);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            synchronized (lock) {
                incomingBuffer.append(text);
                int idx;
                while ((idx = incomingBuffer.indexOf("\u0000")) >= 0) {
                    String frame = incomingBuffer.substring(0, idx);
                    incomingBuffer.delete(0, idx + 1);
                    handleFrame(frame);
                }
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.i(TAG, "WS closing: " + code + " " + reason);
            webSocket.close(code, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.i(TAG, "WS CLOSED: " + code + " " + reason);
            cleanupAfterDisconnect();
            setState(State.DISCONNECTED);
            scheduleReconnect();
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.i(TAG, "WS ERROR: " + t.toString());
            if (response != null) {
                Log.i(TAG, "WS failure response: " + response.code() + " " + response.message());
            }
            cleanupAfterDisconnect();
            setState(State.DISCONNECTED);
            scheduleReconnect();
        }
    }

    private static class PendingSend {
        final String destination;
        final String payload;

        PendingSend(String destination, String payload) {
            this.destination = destination;
            this.payload = payload;
        }
    }
}
