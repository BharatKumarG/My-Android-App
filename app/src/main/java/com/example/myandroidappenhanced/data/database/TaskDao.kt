package com.example.myandroidappenhanced.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY " +
           "CASE " +
           "WHEN isCompleted = 0 AND dueDate IS NOT NULL AND dueDate < datetime('now') THEN 0 " +
           "WHEN isCompleted = 0 THEN priority " +
           "ELSE 999 " +
           "END DESC, " +
           "CASE " +
           "WHEN dueDate IS NOT NULL THEN dueDate " +
           "ELSE createdAt " +
           "END ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY priority DESC, dueDate ASC")
    fun getActiveTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND hasReminder = 1 AND isCompleted = 0")
    suspend fun getTasksWithReminders(): List<Task>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTasks(query: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate ASC")
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND date(dueDate) = date('now') AND isCompleted = 0")
    suspend fun getTasksDueToday(): List<Task>

    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate < datetime('now') AND isCompleted = 0")
    suspend fun getOverdueTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean, completedAt: String?)

    @Query("UPDATE tasks SET hasReminder = :hasReminder WHERE id = :id")
    suspend fun updateTaskReminder(id: Long, hasReminder: Boolean)

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getActiveTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate IS NOT NULL AND dueDate < datetime('now') AND isCompleted = 0")
    fun getOverdueTaskCount(): Flow<Int>
}
