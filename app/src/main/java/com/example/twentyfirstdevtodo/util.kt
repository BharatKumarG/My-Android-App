package com.example.twentyfirstdevtodo

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(millis: Long): String { 
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}
