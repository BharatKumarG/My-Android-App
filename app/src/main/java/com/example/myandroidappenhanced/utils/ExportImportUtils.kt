package com.example.myandroidappenhanced.utils

import android.content.Context
import android.net.Uri
import com.example.myandroidappenhanced.data.database.Task
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportImportUtils @Inject constructor() {

    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    suspend fun exportTasks(context: Context, tasks: List<Task>, uri: Uri): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val jsonString = json.encodeToString(tasks)
                    outputStream.write(jsonString.toByteArray())
                    outputStream.flush()
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun importTasks(context: Context, uri: Uri): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val jsonString = inputStream.readBytes().toString(Charsets.UTF_8)
                    val tasks = json.decodeFromString<List<Task>>(jsonString)

                    // Reset IDs for imported tasks to avoid conflicts
                    val importedTasks = tasks.map { task ->
                        task.copy(
                            id = 0L, // Let Room assign new IDs
                            createdAt = LocalDateTime.now() // Update import timestamp
                        )
                    }

                    Result.success(importedTasks)
                } ?: Result.failure(IOException("Could not open input stream"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun exportTasksToJson(tasks: List<Task>): String {
        return gson.toJson(tasks)
    }

    fun importTasksFromJson(jsonString: String): List<Task> {
        return try {
            val tasks = gson.fromJson(jsonString, Array<Task>::class.java).toList()
            tasks.map { task ->
                task.copy(
                    id = 0L,
                    createdAt = LocalDateTime.now()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        override fun serialize(
            src: LocalDateTime?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(src?.format(formatter))
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDateTime? {
            return try {
                json?.asString?.let { LocalDateTime.parse(it, formatter) }
            } catch (e: Exception) {
                null
            }
        }
    }
}
