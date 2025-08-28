
package com.example.myandroidappenhanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(val tasks: List<Task> = emptyList())

@HiltViewModel
class TasksViewModel @Inject constructor(private val dao: TasksDao): ViewModel() {
    private val base = dao.observeAll()
    val uiState = base.combine(MutableStateFlow(Unit)) { tasks, _ -> UiState(tasks = tasks) }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    // simple suggestions and smart parser (local heuristics)
    val suggestions = listOf("Buy groceries", "Call Mom", "Pay bills", "Schedule meeting")

    fun smartAdd(text: String) = viewModelScope.launch {
        // naive NLP parsing: look for "tomorrow", "today", time, priority keywords
        val lower = text.lowercase()
        val due = when {
            "tomorrow" in lower -> System.currentTimeMillis() + 24*60*60*1000
            "today" in lower -> System.currentTimeMillis()
            else -> null
        }
        val priority = when {
            "urgent" in lower || "important" in lower -> 3
            "soon" in lower -> 2
            else -> 1
        }
        val title = text.replace("tomorrow","").replace("today","").replace("urgent","").trim()
        dao.insert(Task(title = if (title.isBlank()) text else title, note = "", priority = priority, dueAt = due))
    }

    fun delete(task: Task) = viewModelScope.launch { dao.delete(task) }
    fun toggle(task: Task) = viewModelScope.launch { dao.toggle(task.id) }
}
