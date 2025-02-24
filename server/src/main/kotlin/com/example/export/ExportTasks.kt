package com.example.export


import com.example.models.Task
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Export a list of tasks into an ICS file string
 */
fun exportTasksToICSString(tasks: List<Task>): String {
    val icsContent = StringBuilder().apply {
        append("BEGIN:VCALENDAR\n")
        append("VERSION:2.0\n")
        append("PRODID:-//Kotlin//Task Exporter//EN\n")
        tasks.forEach { task ->
            append(convertTaskToICS(task)).append("\n")
        }
        append("END:VCALENDAR")
    }
    return icsContent.toString()
}

/**
 * Export a single of tasks into an event in a ICS format
 */
fun convertTaskToICS(task: Task): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val startDateTime = task.dueDate?.let { dateFormat.format(it) } ?: dateFormat.format(Date())
    val endDateTime = task.dueDate?.let {
        val cal = Calendar.getInstance().apply {
            time = it
            add(Calendar.HOUR, 1) // Assuming the task duration is 1 hour
        }
        dateFormat.format(cal.time)
    } ?: dateFormat.format(Date().apply { time += 3600 * 1000 })

    return """
        BEGIN:VEVENT
        UID:${task.id}
        DTSTAMP:${dateFormat.format(Date())}
        DTSTART:$startDateTime
        DTEND:$endDateTime
        SUMMARY:${task.course}: ${task.taskName ?: "Unnamed Task"}
        DESCRIPTION:Course: ${task.course ?: "N/A"}, Weight: ${task.weight}
        END:VEVENT
    """.trimIndent()
}
