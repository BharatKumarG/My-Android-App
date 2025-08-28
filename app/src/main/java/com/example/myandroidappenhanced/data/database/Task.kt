package com.example.myandroidappenhanced.data.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val dueDateString: String? = null, // Store as string to avoid serialization issues
    val createdAtString: String = LocalDateTime.now().toString(),
    val completedAtString: String? = null
) : Parcelable {

    val dueDateFormatted: String?
        get() {
            return if (dueDateString != null) {
                try {
                    val dateTime = LocalDateTime.parse(dueDateString)
                    dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a"))
                } catch (e: Exception) {
                    dueDateString
                }
            } else null
        }
}

enum class Priority(val displayName: String, val value: Int) {
    LOW("Low", 0),
    MEDIUM("Medium", 1),
    HIGH("High", 2);

    companion object {
        fun fromValue(value: Int): Priority = entries.find { it.value == value } ?: MEDIUM
    }
}
