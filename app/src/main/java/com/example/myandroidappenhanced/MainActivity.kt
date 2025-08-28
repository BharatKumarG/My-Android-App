
package com.example.myandroidappenhanced

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: TasksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        TopAppBar(title = { Text("AI ToDo • Enhanced") }, actions = {
                            IconButton(onClick = { /* search */ }) { Icon(Icons.Default.Search, contentDescription = "Search") }
                        })
                        Spacer(Modifier.height(8.dp))
                        QuickAddRow(onAdd = { text -> vm.smartAdd(text) }, onVoice = { /* voice intent */ })
                        Spacer(Modifier.height(8.dp))
                        SuggestionsRow(suggestions = vm.suggestions, onPick = { vm.smartAdd(it) })
                        Spacer(Modifier.height(12.dp))
                        TaskList(uiState = vm.uiState.collectAsState().value, onToggle = { vm.toggle(it) }, onDelete = { vm.delete(it) })
                    }
                }
            }
        }
    }

}

@Composable
fun QuickAddRow(onAdd: (String)->Unit, onVoice: ()->Unit) {
    var text by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f), label = { Text("Quick add (try: 'Call John tomorrow 9am')") })
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = { onVoice() }) { Icon(Icons.Default.Mic, contentDescription = "Voice") }
        Button(onClick = { if (text.isNotBlank()) { onAdd(text); text = "" } }) { Icon(Icons.Default.Add, contentDescription = null) }
    }
}

@Composable
fun SuggestionsRow(suggestions: List<String>, onPick: (String)->Unit) {
    if (suggestions.isNotEmpty()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            suggestions.forEach { s ->
                AssistChip(onClick = { onPick(s) }, label = { Text(s) })
            }
        }
    }
}

@Composable
fun TaskList(uiState: UiState, onToggle: (Task)->Unit, onDelete: (Task)->Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(uiState.tasks, key = { it.id }) { t ->
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = t.completed, onCheckedChange = { onToggle(t) })
                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Text(t.title)
                        if (t.note.isNotEmpty()) Text(t.note, style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { onDelete(t) }) { Text("Del") }
                }
            }
        }
    }
}
