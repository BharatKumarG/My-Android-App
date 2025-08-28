
package com.example.twentyfirstdevtodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(val tasks: List<Task> = emptyList(), val query: String = "", val category: String? = null)

@HiltViewModel
class TasksViewModel @Inject constructor(private val dao: TasksDao): ViewModel() {
    private val query = MutableStateFlow("")
    private val category = MutableStateFlow<String?>(null)

    val uiState = combine(dao.observeAll(), query, category) { tasks, q, c ->
        val ql = q.trim().lowercase()
        var filtered = tasks
        if (c != null) filtered = filtered.filter { it.category == c }
        if (ql.isNotEmpty()) filtered = filtered.filter { it.title.lowercase().contains(ql) || it.note.lowercase().contains(ql) }
        UiState(tasks = filtered, query = q, category = c)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    fun setQuery(q: String) = viewModelScope.launch { query.emit(q) }
    fun setCategory(c: String?) = viewModelScope.launch { category.emit(c) }

    fun add(task: Task) = viewModelScope.launch { dao.insert(task) }
    fun update(task: Task) = viewModelScope.launch { dao.update(task) }
    fun delete(task: Task) = viewModelScope.launch { dao.delete(task) }
    fun toggle(task: Task) = viewModelScope.launch { dao.toggle(task.id) }
}
