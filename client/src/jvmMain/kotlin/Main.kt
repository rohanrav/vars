import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.darkColors
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import apiservice.APIService
import kotlinx.coroutines.launch
import models.Task
import models.groupTasksByCourse
import preferences.AppPreferences
import presentation.*
import preferences.PreferencesManager
import preferences.onCloseRequestHandler
import presentation.ViewType
import presentation.showView
import presentation.UrlModal
import java.awt.Dimension
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
//import androidx.compose.material3.Icon

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
@Preview
fun App(currentView: MutableState<String>, preferences: AppPreferences) {
    // Temp List to Store Bulk Added Tasks
    val importedTasks = remember { mutableStateListOf<Task>() }

    // App State Declaration
    val uriHandler = LocalUriHandler.current
    val uri = "https://chestnut-spring-739.notion.site/FAQ-OrgaLEARNizer-e7267156665143299f3e04db810c1e72?pvs=4"
    val coroutineScope = rememberCoroutineScope()
    val (taskToEdit, setTaskToEdit) = remember { mutableStateOf<Task?>(null) }
    val (taskToDelete, setTaskToDelete) = remember { mutableStateOf<Task?>(null) }
    val (showDeleteConfirmationModal, setShowDeleteConfirmationModal) = remember { mutableStateOf(false) }
    val (showExportConfirmationModal, setShowExportConfirmationModal) = remember { mutableStateOf(false) }
    val (showTaskModal, setShowTaskModal) = remember { mutableStateOf(false) }
    val (showURLModal, setShowURLModal) = remember { mutableStateOf(false) }
    val (showAPIKeyModal, setShowAPIKeyModal) = remember { mutableStateOf(preferences.openAIAPIKey == null) }
    val (openAIAPIKey, setOpenAIAPIKey) = remember { mutableStateOf(preferences.openAIAPIKey) }
    val (showTextModal, setShowTextModal) = remember { mutableStateOf(false) }
    val (showMassEditTaskModal, setShowMassEditTaskModal) = remember { mutableStateOf(false) }
    val (showErrorModal, setShowErrorModal) = remember { mutableStateOf(false) }
    val (errorMessage, setErrorMessage) = remember { mutableStateOf("") }
    val tasks = remember {
        mutableStateListOf<Task>().also { list ->
            coroutineScope.launch {
                list.addAll(
                    APIService.getAllTasks()
                )
            }
        }
    }
    val startView: ViewType = when (currentView.value) {
        "COURSEVIEW" -> {
            ViewType.COURSEVIEW
        }
        "GRADESVIEW" -> {
            ViewType.GRADESVIEW
        }
        else -> {
            ViewType.COURSEVIEW
        }
    }
    val tasksGroupedByCourse = groupTasksByCourse(tasks).toList().sortedBy { it.first }
    val uniqueCourses: Set<String?> = tasksGroupedByCourse.map { it.first }.toSet()
    val (view, setView) = remember { mutableStateOf(startView) }
    val (expanded, setExpanded) = remember{ mutableStateOf(false)}
    setView(startView)

    // Compose Components
    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = {
                        setShowTaskModal(true)
                    },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Default.Add
                    )
                }
            }
        }
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.aligned(Alignment.End),
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.width(300.dp).padding(5.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row {
                        Text(
                            text = "OrgaLEARNizer",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.W900,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(5.dp)
                        )
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.padding(5.dp).align(Alignment.CenterVertically).clickable {
                                uriHandler.openUri(uri)
                            }
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            if (currentView.value == "COURSEVIEW") {
                                Button(
                                    onClick = {
                                        setView(ViewType.COURSEVIEW)
                                        currentView.value = "COURSEVIEW"
                                    },
                                    modifier = Modifier.width(150.dp).padding(5.dp),
                                    border = BorderStroke(2.5.dp, MaterialTheme.colorScheme.inversePrimary)
                                ) {
                                    Text("Tasks")
                                }

                                ElevatedButton(
                                    onClick = {
                                        setView(ViewType.GRADESVIEW)
                                        currentView.value = "GRADESVIEW"
                                    },
                                    modifier = Modifier.width(150.dp).padding(5.dp),
                                    border = BorderStroke(2.5.dp, MaterialTheme.colorScheme.inversePrimary)
                                ) {
                                    Text("Grades")
                                }
                            }
                            else if (currentView.value == "GRADESVIEW") {
                                ElevatedButton(
                                    onClick = {
                                        setView(ViewType.COURSEVIEW)
                                        currentView.value = "COURSEVIEW"
                                    },
                                    modifier = Modifier.width(150.dp).padding(5.dp),
                                    border = BorderStroke(2.5.dp, MaterialTheme.colorScheme.inversePrimary)
                                ) {
                                    Text("Tasks")
                                }

                                Button(
                                    onClick = {
                                        setView(ViewType.GRADESVIEW)
                                        currentView.value = "GRADESVIEW"
                                    },
                                    modifier = Modifier.width(150.dp).padding(5.dp),
                                    border = BorderStroke(2.5.dp, MaterialTheme.colorScheme.inversePrimary)
                                ) {
                                    Text("Grades")
                                }
                            }
                        }

                        item {
                            Box {
                                OutlinedButton(modifier = Modifier.padding(5.dp).width(150.dp),
                                    onClick = {
                                        setExpanded(true)
                                    }) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Import", modifier = Modifier.padding(end = 5.dp))
                                        Icon(
                                            imageVector = Icons.Default.AddCircle,
                                            contentDescription = null
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = {setExpanded(false)},
                                    modifier = Modifier.width(150.dp),
                                    content = {
                                        if(expanded) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    setShowTextModal(true)
                                                    setExpanded(false)
                                                },
                                                text = {
                                                    Row {
                                                        Icon(
                                                            contentDescription = null,
                                                            imageVector = Icons.Default.ExitToApp,
                                                            modifier = Modifier.rotate(270f)
                                                        )
                                                        Text("From Text/File", modifier = Modifier.padding(start = 5.dp))
                                                    }
                                                }
                                            )
                                            DropdownMenuItem(
                                                onClick = {
                                                    setShowURLModal(true)
                                                    setExpanded(false)
                                                },
                                                text = {
                                                    Row {
                                                        Icon(
                                                            contentDescription = null,
                                                            imageVector = Icons.Default.Search
                                                        )
                                                        Text("From Website", modifier = Modifier.padding(start = 5.dp))
                                                    }
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        item {
                            OutlinedButton(
                                modifier = Modifier.padding(5.dp).width(150.dp),
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            val icsContent = APIService.exportTasksToICS()
                                            val filePath = Paths.get(
                                                System.getProperty("user.home"),
                                                "Downloads",
                                                "tasks.ics"
                                            )
                                            Files.write(filePath, icsContent.toByteArray(), StandardOpenOption.CREATE)
                                            setShowExportConfirmationModal(true)
                                        } catch (e: Exception) {
                                            setErrorMessage("Error exporting tasks to ICS file:\n\n${e.message}")
                                            setShowErrorModal(true)
                                        }
                                    }
                                }
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text("Export", modifier = Modifier.padding(end = 5.dp))
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Export to Calendar"
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Row {
                showView(
                    view,
                    tasksGroupedByCourse,
                    onEdit = { task ->
                        setTaskToEdit(task)
                        setShowTaskModal(true)
                    },
                    onScoreUpdate = { task: Task, newScore: Double ->
                        coroutineScope.launch {
                            try {
                                var newTask = task.copy(score = newScore)
                                APIService.createOrUpdateTask(newTask)
                                tasks.remove(task)
                                tasks.add(newTask)
                            } catch (e: Exception) {
                                setErrorMessage("Error updating score:\n\n${e.message}")
                                setShowErrorModal(true)
                            }
                        }
                    }
                )
            }
        }
    }

    if (showURLModal) {
        UrlModal(
            onSubmit = { course, url, dateIndex, assignmentIndex, onError ->
                coroutineScope.launch {
                    try {
                        val imported = APIService.scrapeTasks(url, dateIndex, assignmentIndex, course)
                        importedTasks.addAll(imported)
                        setShowURLModal(false)
                        setShowMassEditTaskModal(true)
                    } catch (e: Exception) {
                        onError("Error scraping data: ${e.message}")
                    }
                }
            },
            onCancel = { setShowURLModal(false) },
            courses = uniqueCourses
        )
    }

    if (showTaskModal) {
        InputModal(
            onSubmit = { newTask ->
                coroutineScope.launch {
                    try {
                        APIService.createOrUpdateTask(newTask)
                        if (taskToEdit != null) {
                            // Update the existing task
                            tasks.remove(taskToEdit)
                            tasks.add(newTask)
                            setTaskToEdit(null)
                            setTaskToDelete(null)
                        } else {
                            // Insert a new task
                            tasks.add(newTask)
                        }
                    } catch (e: Exception) {
                        setErrorMessage("Error creating or updating task:\n\n${e.message}")
                        setShowErrorModal(true)
                    }
                    setShowTaskModal(false)
                }
            },
            onCancel = {
                setTaskToEdit(null)
                setTaskToDelete(null)
                setShowTaskModal(false)
            },
            taskToEdit = taskToEdit,
            onDelete = { task ->
                setTaskToDelete(task)
                setShowDeleteConfirmationModal(true)
            },
            courses = uniqueCourses
        )
    }

    if (showDeleteConfirmationModal) {
        AlertDialog(
            onDismissRequest = { setShowDeleteConfirmationModal(false) },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                if (taskToDelete != null) {
                                    APIService.deleteTask(taskToDelete)
                                    tasks.remove(taskToDelete)
                                    setShowDeleteConfirmationModal(false)
                                    setShowTaskModal(false)
                                    setTaskToEdit(null)
                                    setTaskToDelete(null)
                                }
                            } catch (e: Exception) {
                                setErrorMessage("Error deleting task\n\n ${e.message}")
                                setShowErrorModal(true)
                            }
                        }

                    }) {
                    Text(
                        text = "Delete",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.hsv(0f, 0.75f, 0.75f)
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        setShowDeleteConfirmationModal(false)
                        setTaskToDelete(null)
                    }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text(
                    text = "Task Delete Confirmation",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            },
            text = {
                Text("Are you sure you want to permanently delete task \"${taskToDelete?.taskName}\" from course ${taskToDelete?.course}?")
            },
            modifier = Modifier.width(400.dp)
        )
    }

    if (showExportConfirmationModal) {
        AlertDialog(
            onDismissRequest = { setShowExportConfirmationModal(false) },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        setShowExportConfirmationModal(false)
                    }
                ) {
                    Text("Ok")
                }
            },
            title = {
                Text(
                    text = "Export Successful",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            },
            text = {
                Text("A file named tasks.ics containing the current tasks has been added to your /Downloads folder.")
            },
            modifier = Modifier.width(400.dp)
        )
    }

    if (showTextModal) {
        if (openAIAPIKey != null) {
            TextModal(
                importedTasks = importedTasks,
                onSubmit = { courseOutline, courseName -> APIService.getTasksFromOutline(courseName, courseOutline, openAIAPIKey) },
                onCancel = { setShowTextModal(false) },
                setShowEditTaskModal = setShowMassEditTaskModal,
                setShowErrorModal = setShowErrorModal,
                setErrorMessage = setErrorMessage
            )
        } else {
            setShowTextModal(false)
            setShowAPIKeyModal(true)
        }
    }

    if (showMassEditTaskModal) {
        MassEditTaskModal(
            tasks = importedTasks,
            onSubmit = {
                try {
                    coroutineScope.launch {
                        val updatedTasks = APIService.createBulkTasks(importedTasks)
                        tasks.clear()
                        tasks.addAll(updatedTasks)
                        importedTasks.clear()

                        setShowMassEditTaskModal(false)
                    }
                } catch (e: Exception) {
                    setErrorMessage("Error bulk adding tasks:\n\n${e.message}")
                    setShowErrorModal(true)
                }
            },
            onCancel = {
                importedTasks.clear()
                setShowMassEditTaskModal(false)
            },
            courses = uniqueCourses
        )
    }

    if (showAPIKeyModal) {
        APIKeyModal(
            setShowAPIKeyModal = setShowAPIKeyModal,
            setOpenAIAPIKey = setOpenAIAPIKey
        )
    }

    ErrorModal(
        showError = showErrorModal,
        errorMessage = errorMessage,
        onDismiss = { setShowErrorModal(false) }
    )
}

fun main() = application {
    val preferences = PreferencesManager.loadPreferences()
    val currentView = remember { mutableStateOf(preferences.viewType) }
    val windowState = rememberWindowState(
        width = preferences.windowWidth.dp,
        height = preferences.windowHeight.dp,
        position = WindowPosition(preferences.windowX.dp, preferences.windowY.dp),
        placement = WindowPlacement.Floating
    )

    Window(
        onCloseRequest = { onCloseRequestHandler(windowState, currentView, ) },
        title = "VARS",
        state = windowState
    ) {
        window.minimumSize = Dimension(1250, 700)
        MaterialTheme {
            App(currentView, preferences)
        }
    }
}
