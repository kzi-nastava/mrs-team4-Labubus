package com.example.ubre.ui.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ubre.R;

public class NotificationHelper {

    private static final String PANIC_CHANNEL_ID = "panic_channel";
    private static final String RIDE_CHANNEL_ID = "ride_channel";

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            manager.createNotificationChannel(new NotificationChannel(
                    PANIC_CHANNEL_ID, "Panic Alerts", NotificationManager.IMPORTANCE_HIGH));

            manager.createNotificationChannel(new NotificationChannel(
                    RIDE_CHANNEL_ID, "Ride Reminder", NotificationManager.IMPORTANCE_DEFAULT));

        }
    }

    public static void showPanic(Context context, String title, String message) {
        show(context, PANIC_CHANNEL_ID, NotificationCompat.PRIORITY_HIGH,
                R.drawable.ic_warning_surface, title, message);
    }

    public static void showRide(Context context, String title, String message) {
        show(context, RIDE_CHANNEL_ID, NotificationCompat.PRIORITY_DEFAULT,
                R.drawable.ic_ride_history, title, message);
    }

    private static void show(Context context, String channelId, int priority,
                             int icon, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify((int) System.currentTimeMillis(), builder.build());
    }
}
