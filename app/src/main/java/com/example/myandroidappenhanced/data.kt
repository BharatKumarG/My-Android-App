
package com.example.myandroidappenhanced

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Entity(tableName = "tasks")
@Serializable
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String = "",
    val priority: Int = 0,
    val dueAt: Long? = null,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface TasksDao {
    @Query("SELECT * FROM tasks ORDER BY completed ASC, priority DESC, createdAt DESC")
    fun observeAll(): Flow<List<Task>>

    @Insert suspend fun insert(task: Task): Long
    @Update suspend fun update(task: Task)
    @Delete suspend fun delete(task: Task)
    @Query("UPDATE tasks SET completed = NOT completed WHERE id = :id") suspend fun toggle(id: Long)
}

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDb: RoomDatabase() { abstract fun tasks(): TasksDao() }
