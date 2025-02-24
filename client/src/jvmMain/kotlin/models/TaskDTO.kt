package models

import kotlinx.serialization.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// Task Data Transfer Object
@Serializable
data class TaskDTO(
    val id: String,
    val taskName: String?,
    val dueDate: String,
    val course: String?,
    val weight: Double,
    val score: Double
)

fun TaskDTO.toTask(): Task {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val dueDateParsed: Date? = try {
        inputFormat.parse(this.dueDate)
    } catch (e: ParseException) {
        println("Parsing date exception: ${e.message}")
        null
    }

    return Task(
        id = UUID.fromString(this.id),
        taskName = this.taskName,
        dueDate = dueDateParsed,
        course = this.course,
        weight = this.weight,
        score = this.score
    )
}