package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistapp.database.TodoDatabase
import com.example.todolistapp.model.Task
import com.example.todolistapp.repository.TaskRepository
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.viewmodel.TaskViewModel
import com.example.todolistapp.viewmodel.TaskViewModelFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TodoDatabase.getDatabase(application)
        val repository = TaskRepository(database.taskDao())

        setContent {
            TodoListAppTheme {
                val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(repository))
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoApp(taskViewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TodoApp(taskViewModel: TaskViewModel, modifier: Modifier = Modifier) {
    var taskText by remember { mutableStateOf("") }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    val tasks by taskViewModel.allTasks.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = taskText,
            onValueChange = { taskText = it },
            label = { Text("Enter task") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (editingTask != null) {
                    taskViewModel.update(editingTask!!.copy(name = taskText))
                    editingTask = null
                } else {
                    if (taskText.isNotEmpty()) {
                        taskViewModel.insert(Task(name = taskText))
                    }
                }
                taskText = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingTask != null) "Edit Task" else "Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tasks:", style = MaterialTheme.typography.bodyLarge)

        TaskList(
            tasks = tasks,
            onDeleteTask = { task -> taskViewModel.delete(task) },
            onEditTask = { task ->
                editingTask = task
                taskText = task.name
            },
            onToggleTask = { task ->
                taskViewModel.update(task.copy(isCompleted = !task.isCompleted))
            }
        )
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onDeleteTask: (Task) -> Unit,
    onEditTask: (Task) -> Unit,
    onToggleTask: (Task) -> Unit
) {
    Column {
        tasks.forEach { task ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onToggleTask(task) },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (task.isCompleted) Color.Green else Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = { onEditTask(task) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Task"
                        )
                    }
                    IconButton(onClick = { onDeleteTask(task) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Task"
                        )
                    }
                }
            }
            Divider()
        }
    }
}
