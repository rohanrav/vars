import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErrorModal(
    showError: Boolean,
    errorMessage: String,
    onDismiss: () -> Unit
) {
    if (showError) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Error", style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold)) },
            text = { Text(errorMessage, style = TextStyle(fontSize = 18.sp)) },
            confirmButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text("OK", color = Color.White)
                }
            },
            modifier = Modifier.padding(20.dp).fillMaxWidth(0.4F)
        )
    }
}
