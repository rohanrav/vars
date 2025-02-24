package presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.Task
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Icon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesView (
    groupedByCourse: List<Pair<String?, List<Task>>>,
    onScoreUpdate: (Task, Double) -> Unit
) {
    val listOfCourses: List<String?> = groupedByCourse.map { it.first }.toList()
    var viewMarks by remember { mutableStateOf(false)}
    val (course, setCourse) = remember { mutableStateOf("")}

    val drawerState = rememberDrawerState(DrawerValue.Open)
    val scope = rememberCoroutineScope()
    val selectedItem = remember { mutableStateOf<String?>("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet (
                modifier = Modifier.width(300.dp)
            ){
                Text("Courses", modifier = Modifier.padding(30.dp),
                    fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyColumn {
                    items(listOfCourses.size) { item ->
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Filled.PlayArrow, contentDescription = null) },
                            label = { Text(listOfCourses[item].toString(), modifier = Modifier.padding(12.dp), fontSize = 15.sp) },
                            selected = listOfCourses[item] == selectedItem.value,
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedItem.value = listOfCourses[item]
                                setCourse(listOfCourses[item].toString())
                                viewMarks = true
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column (
                    modifier = Modifier.width(300.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    OutlinedButton(
                        onClick = { scope.launch { drawerState.open()} },
                        modifier = Modifier.padding(15.dp)
                        ) {
                        Row {
                            Icon (contentDescription = null,
                                imageVector = Icons.Default.List)
                            Text("Select Course")
                        }
                    }
                }

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (viewMarks) {
                        CourseDetailsCard(course, groupedByCourse, onScoreUpdate)
                    }
                }
            }
        }
    )
}

@Composable
fun CourseDetailsCard(
    courseName: (String?),
    groupedByCourse: List<Pair<String?, List<Task>>>,
    onScoreUpdate: (Task, Double) -> Unit
) {
    val tasks = groupedByCourse.filter { it.first == courseName }[0].second.sortedBy { it.taskName }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
            .heightIn(min = 250.dp),
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = courseName.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp, start = 30.dp, top = 20.dp),
                )
            }

            TaskTableHeader()

            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            ) {
                items(tasks.size) { taskIndex ->
                    TaskRow(
                        tasks[taskIndex],
                        onScoreUpdate = onScoreUpdate
                    )
                }
                item {
                    TotalScoreRow(
                        totalScore = tasks.sumOf { it.weight * it.score / 100 },
                        totalWeight = tasks.sumOf { it.weight }
                    )
                }
            }
        }
    }
}


@Composable
fun TaskTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Task", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 10.dp))
            Divider()
        }

        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Weight", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 10.dp))
            Divider()
        }

        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Score", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 10.dp))
            Divider()
        }
    }
}
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskRow(
    task: Task,
    onScoreUpdate: (Task, Double) -> Unit
) {
    var score by remember { mutableStateOf(task.score)}

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val taskName = task.taskName.toString()
            Text(
                text = taskName,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val weight = task.weight.toString()
            Text(
                text = "${weight}%",
                modifier = Modifier.padding(end = 8.dp), textAlign = TextAlign.Left
            )
        }

        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            OutlinedTextField(
                value = score.toString(),
                onValueChange = {
                    val newScore = it.toDoubleOrNull() ?: 0.0
                    score = newScore
                    onScoreUpdate(task, newScore)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.width(80.dp).padding(0.dp)
            )
        }
    }
}

@Composable
fun TotalScoreRow(totalScore: Double, totalWeight: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider()
            Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                modifier = Modifier.padding(15.dp))
        }

        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider()
            Text(text = "${totalWeight}%", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                modifier = Modifier.padding(15.dp))
        }

        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider()
            Text(text = "${totalScore}%", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                modifier = Modifier.padding(15.dp))
        }
    }
}