package com.example.finalprojectshafi.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_table")
public class Notification {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String message;
    public long timestamp;

    public Notification(String title, String message, long timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }
}
