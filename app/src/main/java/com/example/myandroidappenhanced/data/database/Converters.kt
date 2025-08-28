package com.example.myandroidappenhanced.data.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromPriority(priority: Priority): Int {
        return priority.value
    }

    @TypeConverter
    fun toPriority(value: Int): Priority {
        return Priority.fromValue(value)
    }
}
