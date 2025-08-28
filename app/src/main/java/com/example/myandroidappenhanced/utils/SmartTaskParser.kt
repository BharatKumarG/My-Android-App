package com.example.myandroidappenhanced.utils

import com.example.myandroidappenhanced.data.database.Priority
import com.example.myandroidappenhanced.data.database.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartTaskParser @Inject constructor() {

    private val timePatterns = listOf(
        Regex("""(\d{1,2}):(\d{2})\s*(am|pm)""", RegexOption.IGNORE_CASE),
        Regex("""(\d{1,2})\s*(am|pm)""", RegexOption.IGNORE_CASE),
        Regex("""(\d{1,2}):(\d{2})""")
    )

    private val datePatterns = mapOf(
        "today" to { LocalDate.now() },
        "tomorrow" to { LocalDate.now().plusDays(1) },
        "next monday" to { getNextDayOfWeek(1) },
        "next tuesday" to { getNextDayOfWeek(2) },
        "next wednesday" to { getNextDayOfWeek(3) },
        "next thursday" to { getNextDayOfWeek(4) },
        "next friday" to { getNextDayOfWeek(5) },
        "next saturday" to { getNextDayOfWeek(6) },
        "next sunday" to { getNextDayOfWeek(7) },
        "monday" to { getNextDayOfWeek(1) },
        "tuesday" to { getNextDayOfWeek(2) },
        "wednesday" to { getNextDayOfWeek(3) },
        "thursday" to { getNextDayOfWeek(4) },
        "friday" to { getNextDayOfWeek(5) },
        "saturday" to { getNextDayOfWeek(6) },
        "sunday" to { getNextDayOfWeek(7) }
    )

    private val priorityKeywords = mapOf(
        "urgent" to Priority.HIGH,
        "important" to Priority.HIGH,
        "high priority" to Priority.HIGH,
        "asap" to Priority.HIGH,
        "low priority" to Priority.LOW,
        "later" to Priority.LOW,
        "sometime" to Priority.LOW
    )

    private fun getNextDayOfWeek(dayOfWeek: Int): LocalDate {
        val today = LocalDate.now()
        val daysUntilTarget = (dayOfWeek - today.dayOfWeek.value + 7) % 7
        return if (daysUntilTarget == 0) today.plusWeeks(1) else today.plusDays(daysUntilTarget.toLong())
    }

    data class ParsedTask(
        val title: String,
        val dueDate: LocalDateTime? = null,
        val priority: Priority = Priority.MEDIUM,
        val hasReminder: Boolean = false
    )

    fun parseTask(input: String): ParsedTask {
        var cleanInput = input.lowercase().trim()
        var extractedTitle = input.trim()
        var dueDate: LocalDateTime? = null
        var priority = Priority.MEDIUM
        var hasReminder = false

        // Check for reminder keywords
        if (cleanInput.contains("remind me") || cleanInput.contains("reminder")) {
            hasReminder = true
            extractedTitle = extractedTitle.replace(Regex("remind me|reminder", RegexOption.IGNORE_CASE), "").trim()
            cleanInput = extractedTitle.lowercase()
        }

        // Extract priority
        for ((keyword, priorityLevel) in priorityKeywords) {
            if (cleanInput.contains(keyword)) {
                priority = priorityLevel
                extractedTitle = extractedTitle.replace(Regex(keyword, RegexOption.IGNORE_CASE), "").trim()
                cleanInput = extractedTitle.lowercase()
                break
            }
        }

        // Extract date and time
        val dateTime = extractDateTime(cleanInput)
        if (dateTime != null) {
            dueDate = dateTime
            // Remove date/time text from title
            extractedTitle = removeDateTimeFromTitle(extractedTitle, cleanInput)
        }

        return ParsedTask(
            title = extractedTitle.trim().takeIf { it.isNotEmpty() } ?: input,
            dueDate = dueDate,
            priority = priority,
            hasReminder = hasReminder
        )
    }

    private fun extractDateTime(input: String): LocalDateTime? {
        var extractedDate: LocalDate? = null
        var extractedTime: LocalTime? = null

        // Extract date
        for ((keyword, dateFunc) in datePatterns) {
            if (input.contains(keyword)) {
                extractedDate = dateFunc()
                break
            }
        }

        // Extract time
        for (pattern in timePatterns) {
            val match = pattern.find(input)
            if (match != null) {
                extractedTime = parseTime(match)
                break
            }
        }

        // Default time if date is found but no time
        if (extractedDate != null && extractedTime == null) {
            extractedTime = LocalTime.of(9, 0) // Default to 9:00 AM
        }

        return if (extractedDate != null && extractedTime != null) {
            LocalDateTime.of(extractedDate, extractedTime)
        } else {
            null
        }
    }

    private fun parseTime(match: MatchResult): LocalTime? {
        return try {
            val groups = match.groupValues
            when (groups.size) {
                4 -> { // HH:MM AM/PM
                    val hour = groups[1].toInt()
                    val minute = groups[2].toInt()
                    val amPm = groups[3].lowercase()
                    val adjustedHour = when {
                        amPm == "pm" && hour != 12 -> hour + 12
                        amPm == "am" && hour == 12 -> 0
                        else -> hour
                    }
                    LocalTime.of(adjustedHour, minute)
                }
                3 -> { // HH AM/PM or HH:MM
                    if (groups[2].matches(Regex("am|pm", RegexOption.IGNORE_CASE))) {
                        val hour = groups[1].toInt()
                        val amPm = groups[2].lowercase()
                        val adjustedHour = when {
                            amPm == "pm" && hour != 12 -> hour + 12
                            amPm == "am" && hour == 12 -> 0
                            else -> hour
                        }
                        LocalTime.of(adjustedHour, 0)
                    } else {
                        LocalTime.of(groups[1].toInt(), groups[2].toInt())
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun removeDateTimeFromTitle(title: String, cleanInput: String): String {
        var result = title

        // Remove date keywords
        for (keyword in datePatterns.keys) {
            result = result.replace(Regex(keyword, RegexOption.IGNORE_CASE), "")
        }

        // Remove time patterns
        for (pattern in timePatterns) {
            result = pattern.replace(result, "")
        }

        // Remove priority keywords
        for (keyword in priorityKeywords.keys) {
            result = result.replace(Regex(keyword, RegexOption.IGNORE_CASE), "")
        }

        // Clean up extra spaces and punctuation
        return result.replace(Regex("""\s+"""), " ")
            .replace(Regex("""^[,\s]+"""), "")
            .replace(Regex("""[,\s]+$"""), "")
            .trim()
    }

    fun getSuggestions(partialInput: String): List<String> {
        val suggestions = mutableListOf<String>()
        val input = partialInput.lowercase()

        // Common task templates
        val templates = listOf(
            "Call someone tomorrow 9am",
            "Meeting with team next Monday 2pm",
            "Buy groceries Friday evening",
            "Doctor appointment next Tuesday 10am",
            "Submit report by Friday urgent",
            "Review presentation later",
            "Pay bills tomorrow",
            "Workout session 6pm today"
        )

        // Add matching templates
        templates.forEach { template ->
            if (template.lowercase().contains(input) || input.length < 3) {
                suggestions.add(template)
            }
        }

        return suggestions.take(5)
    }
}
