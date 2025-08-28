package com.example.myandroidappenhanced.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myandroidappenhanced.data.database.Priority
import com.example.myandroidappenhanced.data.database.Task
import com.example.myandroidappenhanced.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val tasks: StateFlow<List<Task>> = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllTasks()
            } else {
                repository.searchTasks(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val taskCounts: StateFlow<TaskCounts> = combine(
        repository.getActiveTaskCount(),
        repository.getCompletedTaskCount(),
        repository.getOverdueTaskCount()
    ) { active, completed, overdue ->
        TaskCounts(active, completed, overdue)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskCounts(0, 0, 0)
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addSampleTask() {
        viewModelScope.launch {
            val sampleTask = Task(
                title = "Sample Task ${System.currentTimeMillis() % 1000}",
                description = "This is a sample task created at ${LocalDateTime.now()}",
                priority = Priority.MEDIUM,
                dueDate = LocalDateTime.now().plusDays(1)
            )
            repository.insertTask(sampleTask)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}

data class TaskCounts(
    val active: Int,
    val completed: Int,
    val overdue: Int
)
