package com.example.myandroidappenhanced.utils

import androidx.work.*
import com.example.myandroidappenhanced.data.database.Task
import com.example.myandroidappenhanced.workers.ReminderWorker
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManager @Inject constructor(
    private val workManager: WorkManager
) {

    fun scheduleReminder(task: Task) {
        if (!task.hasReminder || task.dueDate == null || task.isCompleted) {
            return
        }

        val delay = ChronoUnit.MINUTES.between(LocalDateTime.now(), task.dueDate)

        if (delay <= 0) {
            return // Can't schedule past reminders
        }

        val data = Data.Builder()
            .putLong(ReminderWorker.KEY_TASK_ID, task.id)
            .putString(ReminderWorker.KEY_TASK_TITLE, task.title)
            .build()

        val reminderWork = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .setInputData(data)
            .addTag("reminder_${task.id}")
            .build()

        workManager.enqueueUniqueWork(
            "reminder_${task.id}",
            ExistingWorkPolicy.REPLACE,
            reminderWork
        )
    }

    fun cancelReminder(taskId: Long) {
        workManager.cancelUniqueWork("reminder_$taskId")
    }

    fun rescheduleReminder(task: Task) {
        cancelReminder(task.id)
        scheduleReminder(task)
    }

    fun cancelAllReminders() {
        workManager.cancelAllWorkByTag("reminder")
    }
}
