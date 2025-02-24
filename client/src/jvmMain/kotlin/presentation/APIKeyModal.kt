package presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import preferences.saveOpenAIAPIKey

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun APIKeyModal(
    setShowAPIKeyModal: (Boolean) -> Unit,
    setOpenAIAPIKey: (String) -> Unit
) {
    var APIKey by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { setShowAPIKeyModal(false) },
        confirmButton = {
            OutlinedButton(
                enabled = APIKey.isNotEmpty(),
                onClick = {
                    setShowAPIKeyModal(false)
                    saveOpenAIAPIKey(APIKey)
                    setOpenAIAPIKey(APIKey)
                }
            ) {
                Text(
                    text = "Submit",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    setShowAPIKeyModal(false)
                }
            ) {
                Text(
                    text = "I'll add it later",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                )
            }
        },
        title = {
            Text(
                text = "Add your OpenAI API Key",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        },
        text = {
            Column {
                Row {
                    Text("In order to use the AI course outline parsing feature, you will need to add an OpenAI API Key.")
                }
                Row { Spacer(modifier = Modifier.height(8.dp)) }
                Row {
                    OutlinedTextField(
                        value = APIKey,
                        onValueChange = { APIKey = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("API Key") },
                        placeholder = { Text("Enter Your API Key") },
                        singleLine = true
                    )
                }
            }
        },
        modifier = Modifier.width(400.dp)
    )
}
