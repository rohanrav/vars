package presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ElevatedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.Task
import java.text.SimpleDateFormat
import java.util.*




import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InputModal (
    onSubmit: (Task) -> Unit,
    onCancel: () -> Unit,
    taskToEdit: Task? = null,
    onDelete: (Task) -> Unit,
    courses: Set<String?>
) {
    val (task, setTask) = remember { mutableStateOf(taskToEdit?:Task()) }

    if (taskToEdit != null) {
        // Prepopulate the fields with editedTask information
        LaunchedEffect(taskToEdit) {
            setTask(taskToEdit)
        }
    }

    var isTaskNameValid by remember { mutableStateOf(true) }
    var isCourseValid by remember { mutableStateOf(true) }

    fun validateTaskFields(task: Task): Boolean {
        val (_, taskName, _, course, _) = task
        isTaskNameValid = taskName?.isNotBlank() == true
        isCourseValid = course?.isNotBlank() == true
        return isTaskNameValid && isCourseValid
    }

    AlertDialog(
        onDismissRequest = { onCancel() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text =
                    if (taskToEdit != null) {
                        "Edit Task"
                    } else {
                        "New Task"
                    },
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                if (taskToEdit != null) {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Default.Delete,
                        modifier = Modifier.clickable { onDelete(taskToEdit) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                val isValid = validateTaskFields(task)
                if (isValid) {
                    onSubmit(task)
                }
            }) {
                if (taskToEdit != null) {
                    Text("Save", color = Color.White)
                } else {
                    Text("Create", color = Color.White)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onCancel() }) {
                Text("Cancel")
            }
        },
        text = {
            Column {
                NewTaskModalContent(
                    task = task,
                    onTaskChange = setTask,
                    courses = courses
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (!isTaskNameValid) {
                    Text(
                        text = "Please enter a task name",
                        color = Color.Red
                    )
                }
                if (!isCourseValid) {
                    Text(
                        text = "Please enter a course",
                        color = Color.Red
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewTaskModalContent(
    task: Task,
    onTaskChange: (Task) -> Unit,
    courses: Set<String?>
) {
    val (_, taskName, dueDate, course, weight) = task
    val (showDatePicker, setShowDatePicker) = remember { mutableStateOf(false) }
    val (expanded, setExpanded) = remember { mutableStateOf(false)}
    val cal = Calendar.getInstance()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = taskName ?: "",
            modifier = Modifier.width(280.dp),
            onValueChange = { newValue -> onTaskChange(task.copy(taskName = newValue)) },
            label = { Text("Task") }
        )

        OutlinedTextField(
            modifier = Modifier.width(280.dp),
            value = SimpleDateFormat("d MMMM',' yyyy").format(dueDate),
            onValueChange = {},
            label = { Text("Due Date") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    contentDescription = null,
                    imageVector = Icons.Default.DateRange,
                    modifier = Modifier.clickable { setShowDatePicker(true) }
                )
            }
        )

        Row {
            OutlinedTextField(
                modifier = Modifier.width(75.dp),
                value = SimpleDateFormat("H").format(dueDate),
                label = { Text("Time") },
                onValueChange = { newValue ->
                    if (newValue == "") {
                        cal.time = dueDate
                        cal.set(Calendar.HOUR_OF_DAY, 0)
                        cal.set(Calendar.SECOND, 0)
                        onTaskChange(task.copy(dueDate = cal.time))
                    } else if (newValue.toInt() in 0..23) {
                        cal.time = dueDate
                        cal.set(Calendar.HOUR_OF_DAY, newValue.toInt())
                        cal.set(Calendar.SECOND, 0)
                        onTaskChange(task.copy(dueDate = cal.time))
                    }
                },
                placeholder = { Text("0") }
            )

            Spacer(modifier = Modifier.width(5.dp))
            Text(":",)
            Spacer(modifier = Modifier.width(5.dp))

            OutlinedTextField(
                modifier = Modifier.width(75.dp),
                value = SimpleDateFormat("mm").format(dueDate),
                label = { Text("") },
                onValueChange = { newValue ->
                    if (newValue == "") {
                        cal.time = dueDate
                        cal.set(Calendar.MINUTE, 0)
                        cal.set(Calendar.SECOND, 0)
                        onTaskChange(task.copy(dueDate = cal.time))
                    } else if (newValue.toInt() in 0..59) {
                        cal.time = dueDate
                        cal.set(Calendar.MINUTE, newValue.toInt())
                        cal.set(Calendar.SECOND, 0)
                        onTaskChange(task.copy(dueDate = cal.time))
                    }
                }
            )
        }

        Box {
            OutlinedTextField(
                value = course ?: "",
                modifier = Modifier.width(280.dp),
                onValueChange = { newValue -> onTaskChange(task.copy(course = newValue)) },
                label = { Text("Course") },
                trailingIcon = {
                    if (expanded) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                setExpanded(false)
                            }
                        )
                    } else {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                setExpanded(true)
                            }
                        )
                    }
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
                modifier = Modifier.width(280.dp).heightIn(max = 175.dp),
                content = {
                    if (expanded) {
                        for (item in courses) {
                            DropdownMenuItem(
                                text = { Text(item.toString()) },
                                onClick = {
                                    onTaskChange(task.copy(course = item.toString()))
                                    setExpanded(false)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            )
        }

        OutlinedTextField(
            value = weight.toString(),
            modifier = Modifier.width(280.dp),
            onValueChange = { newValue ->
                if ((newValue.isEmpty() || newValue.toDoubleOrNull() != null) && newValue.toDouble() >= 0 && newValue.toDouble() <= 100) {
                    onTaskChange(task.copy(weight = newValue.toDouble()))
                }
            },
            label = { Text("Weight") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
    }

    if (showDatePicker) {
        DatePicker(
            initDate = Date(),
            onDismissRequest = { setShowDatePicker(false) },
            onDateSelect = { newValue ->
                onTaskChange(task.copy(dueDate = newValue))
                setShowDatePicker(false)
            }
        )
    }
}

@Composable
fun DropdownItems(
    show: Boolean
) {

}
