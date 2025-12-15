package com.example.finalprojectshafi.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SnoozeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("task_title");
        String description = intent.getStringExtra("task_description");
        int taskId = intent.getIntExtra("task_id", 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("task_title", title);
        alarmIntent.putExtra("task_description", description);
        alarmIntent.putExtra("task_id", taskId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            long snoozeTime = System.currentTimeMillis() + 10 * 60 * 1000; // 10 minutes
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
        }
    }
}
