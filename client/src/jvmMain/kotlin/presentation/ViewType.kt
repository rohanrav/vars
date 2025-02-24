package presentation

import androidx.compose.runtime.Composable
import models.Task

enum class ViewType {
    COURSEVIEW,
    GRADESVIEW
}

@Composable
fun showView(
    viewType: ViewType,
    groupedByCourse: List<Pair<String?, List<Task>>> = listOf(),
    onEdit: (Task) -> Unit,
    onScoreUpdate: (Task, Double) -> Unit
) {
    when (viewType) {
        ViewType.COURSEVIEW -> {
            CourseView(groupedByCourse, onEdit)
        }
        ViewType.GRADESVIEW -> {
            GradesView(groupedByCourse, onScoreUpdate)
        }
    }
}