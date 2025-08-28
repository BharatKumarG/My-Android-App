
package com.example.twentyfirstdevtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: TasksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var useLight by remember { mutableStateOf(false) }
            val uiState by vm.uiState.collectAsState()

            TwentyOneTheme(darkTheme = !useLight) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("21st Dev") },
                            actions = {
                                IconButton(onClick = { /* search */ }) { Icon(Icons.Default.Search, contentDescription = null) }
                                TextButton(onClick = { useLight = !useLight }) { Text(if (useLight) "Light" else "Dark") }
                                IconButton(onClick = { /* menu */ }) { Icon(Icons.Default.MoreVert, contentDescription = null) }
                            }
                        )
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(onClick = { /* new task */ }, icon = { Icon(Icons.Default.Add, contentDescription = null) }, text = { Text("New") })
                    }
                ) { padding ->
                    Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                        SearchBar(onSearch = { vm.setQuery(it) })
                        Spacer(Modifier.height(8.dp))
                        CategoryChips(selected = uiState.category, onSelect = { vm.setCategory(it) })
                        Spacer(Modifier.height(8.dp))
                        if (uiState.tasks.isEmpty()) Text("No tasks yet") else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(uiState.tasks, key = { it.id }) { task ->
                                    TaskRow(task = task, onToggle = { vm.toggle(task) }, onEdit = {}, onDelete = { vm.delete(task) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    OutlinedTextField(value = value, onValueChange = { value = it; onSearch(it) }, label = { Text("Search") }, singleLine = true, modifier = Modifier.fillMaxWidth())
}

@Composable
fun CategoryChips(selected: String?, onSelect: (String?) -> Unit) {
    val cats = listOf("All","Work","Personal","Shopping","Other")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        cats.forEach { c ->
            FilterChip(selected = (c==selected || (c=="All" && selected==null)), onClick = { onSelect(if (c=="All") null else c) }, label = { Text(c) })
        }
    }
}

@Composable
fun TaskRow(task: Task, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
        Row(modifier = Modifier.padding(12.dp)) {
            Checkbox(checked = task.completed, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(task.title)
                if (task.note.isNotEmpty()) Text(task.note, maxLines = 2)
            }
            Column() {
                if (task.priority > 0) Text("★".repeat(task.priority))
                if (task.dueAt != null) Text(formatDate(task.dueAt))
            }
        }
    }
}
