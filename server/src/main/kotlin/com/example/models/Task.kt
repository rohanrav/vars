package com.example.models

import kotlinx.serialization.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
/**
 * Represents a task.
 *
 * @property id The unique identifier of the task.
 * @property task The task name.
 * @property dueDate The due date for this task.
 * @property course The course that this task is for.
 * @property weight The weight this task accounts for.
 */

data class Task(
    val id: UUID = UUID.randomUUID(),
    val taskName: String? = null,
    val dueDate: Date? = Date(),
    val course: String? = null,
    val weight: Double = 0.0,
    val score: Double = 0.0
)

fun Task.toDTO(): TaskDTO {
    val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    return TaskDTO(
        id = this.id.toString(),
        taskName = this.taskName,
        dueDate = this.dueDate?.let { outputFormat.format(it) } ?: "",
        course = this.course,
        weight = this.weight,
        score = this.score
    )
}


fun MutableList<Task>.add(taskName: String? = null, dueDate: Date? = null, course: String? = null, weight: Double = 0.0) {
    this.add(Task(taskName = taskName, dueDate = dueDate, course = course, weight = weight))
}
