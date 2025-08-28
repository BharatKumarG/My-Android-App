package com.example.myandroidappenhanced.data.repository

import com.example.myandroidappenhanced.data.database.Priority
import com.example.myandroidappenhanced.data.database.Task
import com.example.myandroidappenhanced.data.database.TaskDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()

    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun getTasksWithReminders(): List<Task> = taskDao.getTasksWithReminders()

    fun searchTasks(query: String): Flow<List<Task>> = taskDao.searchTasks(query)

    fun getTasksByPriority(priority: Priority): Flow<List<Task>> = taskDao.getTasksByPriority(priority)

    suspend fun getTasksDueToday(): List<Task> = taskDao.getTasksDueToday()

    suspend fun getOverdueTasks(): List<Task> = taskDao.getOverdueTasks()

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun insertTasks(tasks: List<Task>) = taskDao.insertTasks(tasks)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()

    suspend fun toggleTaskCompletion(task: Task) {
        val updatedTask = if (task.isCompleted) {
            task.copy(isCompleted = false, completedAt = null)
        } else {
            task.copy(isCompleted = true, completedAt = java.time.LocalDateTime.now())
        }
        updateTask(updatedTask)
    }

    suspend fun updateTaskReminder(id: Long, hasReminder: Boolean) = 
        taskDao.updateTaskReminder(id, hasReminder)

    fun getActiveTaskCount(): Flow<Int> = taskDao.getActiveTaskCount()

    fun getCompletedTaskCount(): Flow<Int> = taskDao.getCompletedTaskCount()

    fun getOverdueTaskCount(): Flow<Int> = taskDao.getOverdueTaskCount()
}
