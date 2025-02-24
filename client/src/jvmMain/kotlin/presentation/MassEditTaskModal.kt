package presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import models.Task

@Composable
fun MassEditTaskModal(
    tasks: SnapshotStateList<Task>,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    courses: Set<String?>
) {
    val isLoading by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .heightIn(min = 200.dp, max = 600.dp)
                .padding(20.dp),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Edit Tasks",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                )

                TaskList(tasks, {task -> tasks.remove(task)}, courses)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        enabled = !isLoading,
                        onClick = {
                            onSubmit()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Save")
                    }

                    Button(
                        onClick = onCancel,
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: MutableList<Task>,
    onDelete: (Task) -> Unit,
    courses: Set<String?>
) {
    LazyColumn(
        modifier = Modifier
            .heightIn(max = 400.dp)
            .padding(10.dp)
    ) {
        for (i in tasks.indices step 2) {
            item {
                TaskEditRow(
                    task1 = tasks.getOrNull(i),
                    task2 = tasks.getOrNull(i + 1),
                    tasks = tasks,
                    index1 = i,
                    index2 = i + 1,
                    onDelete = onDelete,
                    courses = courses
                )
            }
        }
    }
}

@Composable
fun TaskEditRow(
    task1: Task?,
    task2: Task?,
    tasks: MutableList<Task>,
    index1: Int,
    index2: Int,
    onDelete: (Task) -> Unit,
    courses: Set<String?>
) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 15.dp)) {
        task1?.let { task ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Task ${index1 + 1}",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Default.Delete,
                        modifier = Modifier.clickable { onDelete(task1) }
                    )
                }
                NewTaskModalContent(
                    task = task,
                    onTaskChange = { newTask ->
                        tasks[index1] = newTask.copy()
                    },
                    courses = courses
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        task2?.let { task ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Task ${index2 + 1}",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Default.Delete,
                        modifier = Modifier.clickable { onDelete(task2) }
                    )
                }
                NewTaskModalContent(
                    task = task,
                    onTaskChange = { newTask ->
                        tasks[index2] = newTask.copy()
                    },
                    courses = courses
                )
            }
        }
    }

    Divider(color = Color.Gray, modifier = Modifier.fillMaxWidth())
}


