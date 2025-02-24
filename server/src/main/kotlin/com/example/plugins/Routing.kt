package com.example.plugins

import com.example.database.Database
import com.example.export.exportTasksToICSString
import com.example.models.Task
import com.example.import_task_automation.getTasksFromCourseOutline
import com.example.import_task_automation.DeadlineToTask
import com.example.import_task_automation.Scrape
import com.example.models.TaskDTO
import com.example.models.toDTO
import com.example.models.toTask
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*

fun Application.configureRouting() {
    install(Webjars) {
        path = "/webjars" //defaults to /webjars
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/webjars") {
            call.respondText("<script src='/webjars/jquery/jquery.js'></script>", ContentType.Text.Html)
        }
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }

        /*
        * Application Routes
        */

        // Create or Update a task
        post("/task") {
            val task = try {
                call.receive<TaskDTO>().toTask()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid task format")
                return@post
            }

            try {
                val existingTask = Database.getTaskById(task.id)
                if (existingTask != null) {
                    Database.updateTask(task)
                    call.respond(HttpStatusCode.OK, "Task updated successfully")
                } else {
                    Database.insertTasks(listOf(task))
                    call.respond(HttpStatusCode.Created, "Task created successfully")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error creating or updating task")
            }
        }

        // Get all tasks
        get("/task") {
            try {
                val tasks = Database.getTasks()
                call.respond(HttpStatusCode.OK, tasks.map { it.toDTO() })
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error fetching all tasks")
            }
        }

        // Delete a task
        delete("/task") {
            val task = try {
                call.receive<TaskDTO>().toTask()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid task format")
                return@delete
            }

            try {
                Database.deleteTask(task)
                call.respond(HttpStatusCode.OK, "Task deleted successfully")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error deleting task")
            }
        }

        // Bulk Add Tasks
        post("/tasks/bulk") {
            val tasksToAdd = try {
                call.receive<List<TaskDTO>>().map { it.toTask() }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid task format")
                return@post
            }

            try {
                Database.insertTasks(tasksToAdd)
                val allTasks = Database.getTasks()
                call.respond(HttpStatusCode.OK, allTasks.map { it.toDTO() })
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error adding bulk tasks: ${e.message}")
            }
        }

        // AI Outline Parsing
        get("/generate-tasks-from-outline") {
            val courseOutline = call.parameters["course_outline"]
            val courseName = call.parameters["course_name"]
            val openAIAPIKey = call.request.headers["Authorization"]?.removePrefix("Bearer ")

            if (courseOutline == null || courseName == null || openAIAPIKey == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing course outline, course name, or API key")
                return@get
            }

            try {
                val tasks = getTasksFromCourseOutline(courseOutline, courseName, openAIAPIKey)
                call.respond(HttpStatusCode.OK, tasks.map { it.toDTO() })
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error generating tasks from outline: ${e.message}")
            }
        }

        // Web Scraping
        get("/scrape") {
            val courseName = call.parameters["course_name"]
            val websiteUrl = call.parameters["website_url"]
            val dateIndex = call.parameters["date_index"]?.toIntOrNull()
            val assignmentIndex = call.parameters["assignment_index"]?.toIntOrNull()

            if (courseName == null || websiteUrl == null || dateIndex == null || assignmentIndex == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
                return@get
            }

            try {
                val deadlines = Scrape(websiteUrl, dateIndex, assignmentIndex)
                val tasksDTO = deadlines.deadlines.map { deadline ->
                    DeadlineToTask(deadline, courseName).toDTO()
                }

                call.respond(HttpStatusCode.OK, tasksDTO)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error during scraping: ${e.message}")
            }
        }

        // Export Calendar
        get("/export-ics") {
            try {
                val tasks = Database.getTasks()
                val icsContent = exportTasksToICSString(tasks)
                call.respondText(icsContent)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error generating ICS file string: ${e.message}")
            }
        }
    }
}
