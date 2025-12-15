package com.example.finalprojectshafi.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public String status;
    public String category;
    public String priority;
    public long startTime;
    public long endTime;
    public long dueDate;
    public double moneySpent;

    public Task(String title, String description, String status, String category, String priority, long startTime, long endTime, long dueDate, double moneySpent) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.category = category;
        this.priority = priority;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dueDate = dueDate;
        this.moneySpent = moneySpent;
    }

    // Helper method to calculate duration in minutes
    public long getDurationInMinutes() {
        if (endTime > startTime) {
            return (endTime - startTime) / (1000 * 60);
        }
        return 0;
    }
}
