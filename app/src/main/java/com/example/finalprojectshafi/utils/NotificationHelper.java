package com.example.finalprojectshafi.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.finalprojectshafi.R;
import com.example.finalprojectshafi.receivers.SnoozeReceiver;

public class NotificationHelper {

    private static final String CHANNEL_ID = "task_reminder_channel";
    private static final String CHANNEL_NAME = "Task Reminders";

    public static void createNotification(Context context, String title, String description, int taskId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        snoozeIntent.putExtra("task_title", title);
        snoozeIntent.putExtra("task_description", description);
        snoozeIntent.putExtra("task_id", taskId);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, taskId, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_launcher_foreground, "Snooze", snoozePendingIntent);

        notificationManager.notify(taskId, builder.build());
    }
}
