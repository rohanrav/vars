package presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import models.Task
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.PDFTextStripperByArea
import androidx.compose.ui.text.input.KeyboardType
import utils.OPENAI_CHARACTER_PROMPT_SIZE_LIMIT

import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme


@Composable
fun TextModal(
    importedTasks: SnapshotStateList<Task>,
    onSubmit: suspend (String, String) -> List<Task>,
    onCancel: () -> Unit,
    setShowEditTaskModal: (Boolean) -> Unit,
    setShowErrorModal: (Boolean) -> Unit,
    setErrorMessage: (String) -> Unit
) {
    var courseOutlineText by remember { mutableStateOf("") }
    var courseNameText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val (showUploadDialog, setShowUploadDialog) = remember { mutableStateOf(false) }

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
            LazyColumn(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 10.dp)
            ) {
                item {
                    Text(
                        "Generate Tasks from Course Outlines",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                    )
                }

                item {
                    OutlinedTextField(
                        enabled = !isLoading,
                        value = courseNameText,
                        onValueChange = { courseNameText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Course Name") },
                        placeholder = { Text("Enter Your Course Name") },
                        singleLine = true,
                    )
                }

                item {
                    OutlinedTextField(
                        enabled = !isLoading,
                        value = courseOutlineText,
                        onValueChange = {
                            courseOutlineText = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp),
                        label = { Text("Course Outline Text") },
                        placeholder = { Text("Copy/Paste Your Course Outline...") },
                        singleLine = false,
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Characters: ${courseOutlineText.length}/$OPENAI_CHARACTER_PROMPT_SIZE_LIMIT",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (OPENAI_CHARACTER_PROMPT_SIZE_LIMIT - courseOutlineText.length < 0)
                                    Color.hsv(0f, 0.75f, 0.75f)
                                else Color.Black,
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            enabled = !isLoading,
                            onClick = { setShowUploadDialog(true) },
                            modifier = Modifier.padding(start = 5.dp)
                        ) {
                            Text("Upload PDF", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.padding(start = 5.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            modifier = Modifier.padding(start = 5.dp),
                            enabled = courseNameText.isNotEmpty() &&
                                    courseOutlineText.isNotEmpty() &&
                                    (OPENAI_CHARACTER_PROMPT_SIZE_LIMIT - courseOutlineText.length) >= 0 &&
                                    !isLoading,
                            onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    try {
                                        val tasks = onSubmit(courseOutlineText, courseNameText)
                                        importedTasks.addAll(tasks)
                                        setShowEditTaskModal(true)
                                        onCancel()
                                    } catch (e: Exception) {
                                        setErrorMessage("Error fetching tasks from OpenAI API:\n\n${e.message}")
                                        setShowErrorModal(true)
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    strokeWidth = 3.dp,
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text("Submit", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    if(showUploadDialog) {
        UploadCard(
            onSubmit = { path, startPage, endPage ->
                courseOutlineText = extractPdfContents(path, startPage, endPage, setShowErrorModal, setErrorMessage)
                setShowUploadDialog(false)
            },
            onCancel = { setShowUploadDialog(false) }
        )
    }
}



@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UploadCard(
    onSubmit: (String, Int, Int) -> Unit,
    onCancel: () -> Unit
) {
    var startPage by remember { mutableStateOf("") }
    var endPage by remember { mutableStateOf("") }

    var path by remember { mutableStateOf("") }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        OutlinedCard(
            modifier = Modifier
//                .height(350.dp)
                .width(400.dp)
                .padding(20.dp),
//            elevation = 10.dp,
            content = {
                Column (
                    modifier = Modifier.padding(15.dp)
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            "Upload Outline PDF",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = path,
                            onValueChange = { newPath -> path = newPath
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(50.dp).height(50.dp)
                                        .clickable { path = uploadPdfFile() }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.width(150.dp),
                                value = startPage,
                                onValueChange = { newValue ->
                                    startPage = newValue.filter { char -> char.isDigit() }
                                },
                                label = { Text("Start page") },
                                textStyle = TextStyle(
                                    fontSize = 15.sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedTextField(
                                modifier = Modifier.width(150.dp),
                                value = endPage,
                                onValueChange = { newValue ->
                                    endPage = newValue.filter { char -> char.isDigit() }
                                },
                                label = { Text("End page") },
                                textStyle = TextStyle(
                                    fontSize = 15.sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                    Row (
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            modifier = Modifier.padding(end = 10.dp, top = 10.dp),
                            onClick = onCancel,
                            content = { Text("Cancel") }
                        )

                        Button(
                            modifier = Modifier.padding(end = 10.dp, top = 10.dp),
                            onClick = { onSubmit(path, startPage.toInt(), endPage.toInt()) },
                            content = { Text("Upload")},
                            enabled = path.isNotBlank() && startPage.isNotBlank() && endPage.isNotBlank()
                        )
                    }
                }
            }
        )
    }
}


fun uploadPdfFile(): String {
    var path = ""
    val fileChooser = JFileChooser()
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
    val pdfFilter = FileNameExtensionFilter("PDF files (*.pdf)", "pdf")
    fileChooser.fileFilter = pdfFilter
    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        val selectedFile = fileChooser.selectedFile
        path = selectedFile.absolutePath
    } else {
        println("No file selected.")
    }
    return path
}


fun extractPdfContents(
    filePath: String,
    start: Int,
    end: Int,
    setShowErrorModal: (Boolean) -> Unit,
    setErrorMessage: (String) -> Unit
    ): String {
    var contents = ""

    try {
        // Loading an existing document based on the provided file path
        val file = File(filePath)
        val document = PDDocument.load(file)
//        val selectedPage = document.getPage(4)
        // Instantiate PDFTextStripper class
        val pdfStripper = PDFTextStripper()
        val stripper = PDFTextStripperByArea()
        stripper.sortByPosition = true
        pdfStripper.startPage = start
        pdfStripper.endPage = end

        // Get text from the whole document
        contents = pdfStripper.getText(document)

        // Uncomment the following lines if you want to extract text from a specific area/region
        /*
        val rect = Rectangle(1, 1, 611, 794)
        stripper.addRegion("class1", rect)
        val firstPage = document.getPage(4)
        stripper.extractRegions(firstPage)
        contents += "\nText in the area: $rect"
        contents += "\n${stripper.getTextForRegion("class1")}"
        */

        // Closing the document
        document.close()
    } catch (e: Exception) {
        setErrorMessage("Invalid PDF file:\n\n${e.message}")
        setShowErrorModal(true)
    }

    return contents
}
