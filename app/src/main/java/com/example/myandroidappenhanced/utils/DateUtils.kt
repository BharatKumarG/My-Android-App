package com.example.myandroidappenhanced.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {

    val displayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a")
    val shortDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd")
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    fun formatForDisplay(dateTime: LocalDateTime): String {
        return dateTime.format(displayFormatter)
    }

    fun formatShortDate(dateTime: LocalDateTime): String {
        return dateTime.format(shortDateFormatter)
    }

    fun formatTime(dateTime: LocalDateTime): String {
        return dateTime.format(timeFormatter)
    }

    fun getRelativeTime(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val daysDiff = ChronoUnit.DAYS.between(now.toLocalDate(), dateTime.toLocalDate())

        return when {
            daysDiff == 0L -> "Today ${formatTime(dateTime)}"
            daysDiff == 1L -> "Tomorrow ${formatTime(dateTime)}"
            daysDiff == -1L -> "Yesterday ${formatTime(dateTime)}"
            daysDiff > 1 && daysDiff <= 7 -> "${dateTime.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }} ${formatTime(dateTime)}"
            daysDiff < -1 && daysDiff >= -7 -> "Last ${dateTime.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }} ${formatTime(dateTime)}"
            else -> formatForDisplay(dateTime)
        }
    }

    fun isOverdue(dateTime: LocalDateTime): Boolean {
        return dateTime.isBefore(LocalDateTime.now())
    }

    fun isDueToday(dateTime: LocalDateTime): Boolean {
        return dateTime.toLocalDate().isEqual(LocalDateTime.now().toLocalDate())
    }

    fun isDueTomorrow(dateTime: LocalDateTime): Boolean {
        return dateTime.toLocalDate().isEqual(LocalDateTime.now().toLocalDate().plusDays(1))
    }

    fun getTimeUntilDue(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val hours = ChronoUnit.HOURS.between(now, dateTime)
        val minutes = ChronoUnit.MINUTES.between(now, dateTime)

        return when {
            hours < 0 -> "Overdue"
            hours == 0L -> "${minutes}m"
            hours < 24 -> "${hours}h ${minutes % 60}m"
            else -> "${hours / 24}d ${hours % 24}h"
        }
    }
}
