package com.example.finalprojectshafi.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.finalprojectshafi.database.Notification;
import com.example.finalprojectshafi.database.Task;
import com.example.finalprojectshafi.database.TaskDatabase;
import com.example.finalprojectshafi.utils.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("notification_type");
        int taskId = intent.getIntExtra("task_id", 0);
        String title = intent.getStringExtra("task_title");
        String status = intent.getStringExtra("task_status");

        if (type == null) return;

        TaskDatabase db = TaskDatabase.getInstance(context);

        String notificationTitle = "";
        String notificationMessage = "";

        switch (type) {
            case "upcoming":
                notificationTitle = "Upcoming: " + title;
                notificationMessage = "Your task is due in 30 minutes.";
                break;
            case "due":
                notificationTitle = "Due: " + title;
                notificationMessage = "Your task is due now.";
                break;
            case "missed":
                if (status != null && status.equals("Pending")) {
                    Task task = db.taskDao().getTaskById(taskId);
                    if (task != null && task.status.equals("Pending")) {
                        notificationTitle = "Missed: " + title;
                        notificationMessage = "You missed a task!";
                    }
                }
                break;
        }

        if (!notificationTitle.isEmpty()) {
            NotificationHelper.createNotification(context, notificationTitle, notificationMessage, taskId);
            db.notificationDao().insert(new Notification(notificationTitle, notificationMessage, System.currentTimeMillis()));
        }
    }
}
