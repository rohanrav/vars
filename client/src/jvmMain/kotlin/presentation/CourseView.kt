package presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import models.Task
import java.text.SimpleDateFormat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CourseView(
    groupedByCourse: List<Pair<String?, List<Task>>>,
    onEdit: (Task) -> Unit) {

    val colors = listOf<Color>(
        Color.hsl(200f, 0.6f, 0.85f),
        Color.hsl(225f, 0.6f, 0.85f),
        Color.hsl(250f, 0.6f, 0.85f),
        Color.hsl(275f, 0.6f, 0.85f),
        Color.hsl(300f, 0.6f, 0.85f)

        )

    LazyRow(
        modifier = Modifier.padding(top = 10.dp, bottom = 50.dp, end = 5.dp).fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        items(groupedByCourse.size) { group ->
            Spacer(modifier = Modifier.width(15.dp))
            Column (
                modifier = Modifier
                    .background(
                        color = colors[group.mod(colors.size)],
                        shape = RoundedCornerShape(10.dp))
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    groupedByCourse[group].first.toString(),
                    modifier = Modifier.width(250.dp)
                        .padding(bottom = 15.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                val sortedTasks = groupedByCourse[group].second.sortedBy { it.dueDate }

                LazyColumn(
                    horizontalAlignment = Alignment.Start
                ) {
                    items(sortedTasks.size) {
                        OutlinedCard(
                            modifier = Modifier
                                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                                .width(250.dp)
                                .clickable { onEdit(sortedTasks[it]) }
//                            elevation = 5.dp
                        ) {
                            Row {
                                Spacer(modifier = Modifier.width(15.dp))
                                Column {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = sortedTasks[it].taskName.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = SimpleDateFormat("dd MMM yyyy").format(sortedTasks[it].dueDate)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = sortedTasks[it].weight.toString() + "%"
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                                Spacer(modifier = Modifier.width(15.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}
