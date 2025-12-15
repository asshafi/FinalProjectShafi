package com.example.finalprojectshafi.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    // --- Advanced Sorting Queries ---
    @Query("SELECT * FROM task_table ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 ELSE 4 END, dueDate ASC")
    List<Task> getTasksSortedByPriorityAndDueDate();

    @Query("SELECT * FROM task_table ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 ELSE 4 END, (endTime - startTime) DESC")
    List<Task> getTasksSortedByPriorityThenDuration();

    @Query("SELECT * FROM task_table ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 ELSE 4 END, moneySpent DESC")
    List<Task> getTasksSortedByPriorityThenMoney();

    @Query("SELECT * FROM task_table ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 ELSE 4 END, startTime ASC")
    List<Task> getTasksSortedByPriorityThenStartTime();

    @Query("SELECT * FROM task_table ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 ELSE 4 END, endTime ASC")
    List<Task> getTasksSortedByPriorityThenEndTime();

    // --- Other Queries ---
    @Query("SELECT * FROM task_table WHERE title LIKE '%' || :key || '%' ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 ELSE 4 END, dueDate ASC")
    List<Task> searchTask(String key);

    @Query("SELECT * FROM task_table WHERE status = 'Pending' ORDER BY dueDate ASC")
    List<Task> getPendingTasksSortedByDueDate();

    @Query("SELECT * FROM task_table WHERE id = :id")
    Task getTaskById(int id);

    @Query("SELECT COUNT(*) FROM task_table WHERE status='Completed'")
    int successCount();

    @Query("SELECT COUNT(*) FROM task_table WHERE status='Pending'")
    int failureCount();

    @Query("SELECT SUM(moneySpent) FROM task_table WHERE category = :category")
    double getMoneySpentByCategory(String category);
}
