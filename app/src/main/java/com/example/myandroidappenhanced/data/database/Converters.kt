package com.example.myandroidappenhanced.data.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromPriority(priority: Priority): Int {
        return priority.value
    }

    @TypeConverter
    fun toPriority(value: Int): Priority {
        return Priority.fromValue(value)
    }
}
