package com.example.todolistapp_nodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import com.example.todolistapp_nodb.ui.theme.TodoListAppNoDBTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoListAppNoDBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TodoApp(modifier: Modifier = Modifier) {
    var taskText by remember { mutableStateOf("") }
    var todoList by remember { mutableStateOf(listOf<String>()) }
    var isEditing by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = taskText,
            onValueChange = { taskText = it },
            label = { Text(if (isEditing) "Edit task" else "Enter task") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (taskText.isNotEmpty()) {
                    if (isEditing && taskToEdit != null) {
                        todoList = todoList.map { task ->
                            if (task == taskToEdit) taskText else task
                        }
                        taskToEdit = null
                        isEditing = false
                    } else {
                        todoList = todoList + taskText
                    }
                    taskText = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Save Changes" else "Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tasks:", style = MaterialTheme.typography.bodyLarge)

        TaskList(tasks = todoList, onDeleteTask = { task ->
            todoList = todoList.filter { it != task }
        }, onEditTask = { task ->
            taskText = task
            taskToEdit = task
            isEditing = true
        })
    }
}

@Composable
fun TaskList(tasks: List<String>, onDeleteTask: (String) -> Unit, onEditTask: (String) -> Unit) {
    Column {
        tasks.forEach { task ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = task, style = MaterialTheme.typography.bodyMedium)

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

@Preview(showBackground = true)
@Composable
fun TodoAppPreview() {
    TodoListAppNoDBTheme {
        TodoApp()
    }
}
