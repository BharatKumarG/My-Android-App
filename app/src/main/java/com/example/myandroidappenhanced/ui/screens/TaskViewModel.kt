package com.example.myandroidappenhanced.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myandroidappenhanced.data.database.Priority
import com.example.myandroidappenhanced.data.database.Task
import com.example.myandroidappenhanced.data.repository.TaskRepository
import com.example.myandroidappenhanced.utils.ExportImportUtils
import com.example.myandroidappenhanced.utils.ReminderManager
import com.example.myandroidappenhanced.utils.SmartTaskParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val smartTaskParser: SmartTaskParser,
    private val reminderManager: ReminderManager,
    private val exportImportUtils: ExportImportUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

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

    val activeTasks: StateFlow<List<Task>> = repository.getActiveTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val completedTasks: StateFlow<List<Task>> = repository.getCompletedTasks()
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

    init {
        loadOverdueTasks()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun setSelectedTab(tab: TaskTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun showAddTaskDialog() {
        _uiState.update { 
            it.copy(
                showAddTaskDialog = true,
                editingTask = null,
                taskTitle = "",
                taskDescription = "",
                selectedPriority = Priority.MEDIUM,
                selectedDueDate = null,
                hasReminder = false
            )
        }
    }

    fun showEditTaskDialog(task: Task) {
        _uiState.update { 
            it.copy(
                showAddTaskDialog = true,
                editingTask = task,
                taskTitle = task.title,
                taskDescription = task.description,
                selectedPriority = task.priority,
                selectedDueDate = task.dueDate,
                hasReminder = task.hasReminder
            )
        }
    }

    fun hideTaskDialog() {
        _uiState.update { 
            it.copy(
                showAddTaskDialog = false,
                editingTask = null,
                taskTitle = "",
                taskDescription = "",
                selectedPriority = Priority.MEDIUM,
                selectedDueDate = null,
                hasReminder = false,
                smartSuggestions = emptyList()
            )
        }
    }

    fun updateTaskTitle(title: String) {
        _uiState.update { it.copy(taskTitle = title) }

        // Generate smart suggestions
        if (title.length > 2) {
            val suggestions = smartTaskParser.getSuggestions(title)
            _uiState.update { it.copy(smartSuggestions = suggestions) }
        } else {
            _uiState.update { it.copy(smartSuggestions = emptyList()) }
        }
    }

    fun updateTaskDescription(description: String) {
        _uiState.update { it.copy(taskDescription = description) }
    }

    fun updateSelectedPriority(priority: Priority) {
        _uiState.update { it.copy(selectedPriority = priority) }
    }

    fun updateSelectedDueDate(dateTime: LocalDateTime?) {
        _uiState.update { it.copy(selectedDueDate = dateTime) }
    }

    fun updateHasReminder(hasReminder: Boolean) {
        _uiState.update { it.copy(hasReminder = hasReminder) }
    }

    fun applySuggestion(suggestion: String) {
        val parsed = smartTaskParser.parseTask(suggestion)
        _uiState.update { 
            it.copy(
                taskTitle = parsed.title,
                selectedPriority = parsed.priority,
                selectedDueDate = parsed.dueDate,
                hasReminder = parsed.hasReminder,
                smartSuggestions = emptyList()
            )
        }
    }

    fun parseQuickAdd(input: String) {
        val parsed = smartTaskParser.parseTask(input)
        _uiState.update { 
            it.copy(
                taskTitle = parsed.title,
                selectedPriority = parsed.priority,
                selectedDueDate = parsed.dueDate,
                hasReminder = parsed.hasReminder
            )
        }
    }

    fun saveTask() {
        val currentState = _uiState.value
        if (currentState.taskTitle.isBlank()) return

        viewModelScope.launch {
            try {
                val task = if (currentState.editingTask != null) {
                    currentState.editingTask.copy(
                        title = currentState.taskTitle.trim(),
                        description = currentState.taskDescription.trim(),
                        priority = currentState.selectedPriority,
                        dueDate = currentState.selectedDueDate,
                        hasReminder = currentState.hasReminder
                    )
                } else {
                    Task(
                        title = currentState.taskTitle.trim(),
                        description = currentState.taskDescription.trim(),
                        priority = currentState.selectedPriority,
                        dueDate = currentState.selectedDueDate,
                        hasReminder = currentState.hasReminder,
                        createdAt = LocalDateTime.now()
                    )
                }

                val taskId = if (currentState.editingTask != null) {
                    repository.updateTask(task)
                    task.id
                } else {
                    repository.insertTask(task)
                }

                // Schedule or update reminder
                if (task.hasReminder && task.dueDate != null) {
                    val taskWithId = task.copy(id = taskId)
                    reminderManager.scheduleReminder(taskWithId)
                } else {
                    reminderManager.cancelReminder(taskId)
                }

                hideTaskDialog()
                showMessage("Task saved successfully")
            } catch (e: Exception) {
                showMessage("Error saving task: ${e.message}")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.deleteTask(task)
                reminderManager.cancelReminder(task.id)

                // Store for potential undo
                _uiState.update { 
                    it.copy(
                        deletedTask = task,
                        showUndoSnackbar = true
                    )
                }

                showMessage("Task deleted")
            } catch (e: Exception) {
                showMessage("Error deleting task: ${e.message}")
            }
        }
    }

    fun undoDelete() {
        val deletedTask = _uiState.value.deletedTask
        if (deletedTask != null) {
            viewModelScope.launch {
                try {
                    val newTaskId = repository.insertTask(deletedTask.copy(id = 0))
                    if (deletedTask.hasReminder && deletedTask.dueDate != null) {
                        reminderManager.scheduleReminder(deletedTask.copy(id = newTaskId))
                    }
                    _uiState.update { 
                        it.copy(
                            deletedTask = null,
                            showUndoSnackbar = false
                        )
                    }
                    showMessage("Task restored")
                } catch (e: Exception) {
                    showMessage("Error restoring task: ${e.message}")
                }
            }
        }
    }

    fun hideUndoSnackbar() {
        _uiState.update { 
            it.copy(
                deletedTask = null,
                showUndoSnackbar = false
            )
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            try {
                repository.toggleTaskCompletion(task)

                if (task.isCompleted) {
                    // Task was completed, now uncomplete it - might need to reschedule reminder
                    if (task.hasReminder && task.dueDate != null) {
                        reminderManager.scheduleReminder(task)
                    }
                } else {
                    // Task was incomplete, now complete it - cancel reminder
                    reminderManager.cancelReminder(task.id)
                }
            } catch (e: Exception) {
                showMessage("Error updating task: ${e.message}")
            }
        }
    }

    private fun loadOverdueTasks() {
        viewModelScope.launch {
            try {
                val overdueTasks = repository.getOverdueTasks()
                if (overdueTasks.isNotEmpty()) {
                    showMessage("You have ${overdueTasks.size} overdue tasks")
                }
            } catch (e: Exception) {
                // Ignore errors for this background operation
            }
        }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun exportTasks(tasks: List<Task>): String {
        return exportImportUtils.exportTasksToJson(tasks)
    }

    fun importTasks(jsonString: String) {
        viewModelScope.launch {
            try {
                val importedTasks = exportImportUtils.importTasksFromJson(jsonString)
                if (importedTasks.isNotEmpty()) {
                    repository.insertTasks(importedTasks)

                    // Schedule reminders for imported tasks
                    importedTasks.forEach { task ->
                        if (task.hasReminder && task.dueDate != null) {
                            reminderManager.scheduleReminder(task)
                        }
                    }

                    showMessage("Imported ${importedTasks.size} tasks successfully")
                } else {
                    showMessage("No tasks found in the file")
                }
            } catch (e: Exception) {
                showMessage("Error importing tasks: ${e.message}")
            }
        }
    }
}

data class TaskUiState(
    val selectedTab: TaskTab = TaskTab.ALL,
    val showAddTaskDialog: Boolean = false,
    val editingTask: Task? = null,
    val taskTitle: String = "",
    val taskDescription: String = "",
    val selectedPriority: Priority = Priority.MEDIUM,
    val selectedDueDate: LocalDateTime? = null,
    val hasReminder: Boolean = false,
    val smartSuggestions: List<String> = emptyList(),
    val deletedTask: Task? = null,
    val showUndoSnackbar: Boolean = false,
    val message: String? = null
)

data class TaskCounts(
    val active: Int,
    val completed: Int,
    val overdue: Int
)

enum class TaskTab(val displayName: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Completed")
}
