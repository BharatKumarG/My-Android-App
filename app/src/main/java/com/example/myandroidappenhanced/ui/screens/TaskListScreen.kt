package com.example.myandroidappenhanced.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myandroidappenhanced.data.database.Task
import com.example.myandroidappenhanced.ui.components.AddTaskDialog
import com.example.myandroidappenhanced.ui.components.TaskCard
import com.example.myandroidappenhanced.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val taskCounts by viewModel.taskCounts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var searchExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    // Handle undo snackbar
    LaunchedEffect(uiState.showUndoSnackbar) {
        if (uiState.showUndoSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = "Task deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.hideUndoSnackbar()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = !searchExpanded,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        Text("My Tasks")
                    }

                    AnimatedVisibility(
                        visible = searchExpanded,
                        enter = fadeIn(animationSpec = tween(300)) + expandHorizontally(),
                        exit = fadeOut(animationSpec = tween(300)) + shrinkHorizontally()
                    ) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = viewModel::updateSearchQuery,
                            onSearchActiveChange = { active ->
                                if (!active) {
                                    searchExpanded = false
                                    viewModel.clearSearch()
                                }
                            },
                            active = false,
                            onSearch = { },
                            placeholder = { Text("Search tasks...") },
                            leadingIcon = {
                                IconButton(
                                    onClick = {
                                        searchExpanded = false
                                        viewModel.clearSearch()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {}
                    }
                },
                actions = {
                    if (!searchExpanded) {
                        IconButton(
                            onClick = { searchExpanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }

                        IconButton(
                            onClick = {
                                // Export functionality - in real app would open file picker
                                val exportedJson = viewModel.exportTasks(tasks)
                                // For demo, just show a message
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddTaskDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Task summary cards
            TaskSummaryCards(
                taskCounts = taskCounts,
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::setSelectedTab,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Task list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (tasks.isEmpty()) {
                    item {
                        EmptyState(
                            selectedTab = uiState.selectedTab,
                            hasSearchQuery = searchQuery.isNotBlank()
                        )
                    }
                } else {
                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onEdit = { viewModel.showEditTaskDialog(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }
    }

    // Add/Edit Task Dialog
    AddTaskDialog(
        isVisible = uiState.showAddTaskDialog,
        title = uiState.taskTitle,
        description = uiState.taskDescription,
        priority = uiState.selectedPriority,
        dueDate = uiState.selectedDueDate,
        hasReminder = uiState.hasReminder,
        smartSuggestions = uiState.smartSuggestions,
        isEditing = uiState.editingTask != null,
        onTitleChange = viewModel::updateTaskTitle,
        onDescriptionChange = viewModel::updateTaskDescription,
        onPriorityChange = viewModel::updateSelectedPriority,
        onDueDateChange = viewModel::updateSelectedDueDate,
        onReminderChange = viewModel::updateHasReminder,
        onSuggestionClick = viewModel::applySuggestion,
        onQuickAddClick = viewModel::parseQuickAdd,
        onSave = viewModel::saveTask,
        onDismiss = viewModel::hideTaskDialog
    )
}

@Composable
fun TaskSummaryCards(
    taskCounts: TaskCounts,
    selectedTab: TaskTab,
    onTabSelected: (TaskTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            title = "All",
            count = taskCounts.active + taskCounts.completed,
            icon = Icons.Default.List,
            isSelected = selectedTab == TaskTab.ALL,
            onClick = { onTabSelected(TaskTab.ALL) },
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "Active",
            count = taskCounts.active,
            icon = Icons.Default.CheckCircleOutline,
            isSelected = selectedTab == TaskTab.ACTIVE,
            onClick = { onTabSelected(TaskTab.ACTIVE) },
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "Done",
            count = taskCounts.completed,
            icon = Icons.Default.CheckCircle,
            isSelected = selectedTab == TaskTab.COMPLETED,
            onClick = { onTabSelected(TaskTab.COMPLETED) },
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryCard(
    title: String,
    count: Int,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EmptyState(
    selectedTab: TaskTab,
    hasSearchQuery: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (hasSearchQuery) Icons.Default.SearchOff else Icons.Default.TaskAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                hasSearchQuery -> "No tasks found"
                selectedTab == TaskTab.COMPLETED -> "No completed tasks"
                selectedTab == TaskTab.ACTIVE -> "No active tasks"
                else -> "No tasks yet"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when {
                hasSearchQuery -> "Try different search terms"
                selectedTab == TaskTab.COMPLETED -> "Complete some tasks to see them here"
                selectedTab == TaskTab.ACTIVE -> "Add your first task to get started"
                else -> "Add your first task to get started"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}
