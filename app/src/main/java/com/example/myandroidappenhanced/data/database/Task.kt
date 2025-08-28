package com.example.myandroidappenhanced.data.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
@Serializable
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dueDate: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val completedAt: LocalDateTime? = null
) : Parcelable {

    val dueDateFormatted: String?
        get() = dueDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a"))
}

@Serializable
enum class Priority(val displayName: String, val value: Int) {
    LOW("Low", 0),
    MEDIUM("Medium", 1),
    HIGH("High", 2);

    companion object {
        fun fromValue(value: Int): Priority = entries.find { it.value == value } ?: MEDIUM
    }
}
