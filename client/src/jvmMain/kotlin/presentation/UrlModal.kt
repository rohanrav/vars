package presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import isValidUrl

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UrlModal(
    onSubmit: (String, String, Int, Int, (String) -> Unit) -> Unit,
    onCancel: () -> Unit,
    courses: Set<String?>
) {
    var course by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var dateIndex by remember { mutableStateOf("") }
    var assignmentIndex by remember { mutableStateOf("") }

    var isUrlValid by remember { mutableStateOf(true) }
    var isCourseValid by remember { mutableStateOf(true) }
    var isDateIndexValid by remember { mutableStateOf(true) }
    var isAssignmentIndexValid by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val (expanded, setExpanded) = remember { mutableStateOf(false)}

    AlertDialog(
        onDismissRequest = {
            onCancel()
            errorMessage = ""
            isLoading = false
        },
        title = { NewUrlTitle() },
        confirmButton = {
            Button(
                onClick = {
                    isUrlValid = isValidUrl(url)
                    isCourseValid = course.isNotBlank()
                    isDateIndexValid = dateIndex.isNotBlank()
                    isAssignmentIndexValid = assignmentIndex.isNotBlank()
                    if (isCourseValid && url.isNotBlank() && isDateIndexValid && isAssignmentIndexValid && isUrlValid) {
                        errorMessage = ""
                        isLoading = true
                        try {
                            onSubmit(course, url, dateIndex.toInt(), assignmentIndex.toInt()) { error ->
                                errorMessage = error
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                    )
                } else {
                    Text("Import")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = {
                onCancel()
                errorMessage = ""
                isLoading = false
            }) {
                Text("Cancel")
            }
        },
        text = {
            Column {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    OutlinedTextField(
                        value = course,
                        onValueChange = { course = it },
                        modifier = Modifier.width(280.dp),
                        label = { Text("Course Name") },
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

                    androidx.compose.material3.DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { setExpanded(false) },
                        modifier = Modifier.width(280.dp).heightIn(max = 175.dp),
                        content = {
                            if (expanded) {
                                for (item in courses) {
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text(item.toString()) },
                                        onClick = {
                                            course = item.toString()
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
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    modifier = Modifier.width(280.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dateIndex,
                    modifier = Modifier.width(280.dp),
                    onValueChange = {
                        dateIndex = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("Date Index") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = assignmentIndex,
                    modifier = Modifier.width(280.dp),
                    onValueChange = {
                        assignmentIndex = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("Assignment Index") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
                Spacer(modifier = Modifier.height(16.dp))

                if (!isUrlValid) {
                    Text(
                        text = "Please enter a valid URL",
                        color = Color.Red
                    )
                }

                if (!isCourseValid) {
                    Text(
                        text = "Please enter a course",
                        color = Color.Red
                    )
                }

                if (!isDateIndexValid) {
                    Text(
                        text = "Please enter a date index",
                        color = Color.Red
                    )
                }

                if (!isAssignmentIndexValid) {
                    Text(
                        text = "Please enter an assignment index",
                        color = Color.Red
                    )
                }

                if (errorMessage != "") {
                    Text(
                        text = "Error scraping data",
                        color = Color.Red
                    )
                }
            }
        }
    )
}

@Composable
fun NewUrlTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Import Tasks From URL",
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
    }
}