package com.example.finalprojectshafi.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class, Notification.class}, version = 5, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    private static TaskDatabase instance;

    public abstract TaskDao taskDao();
    public abstract NotificationDao notificationDao();

    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    TaskDatabase.class,
                    "task_database"
            ).fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return instance;
    }
}
