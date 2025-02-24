package apiservice

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.builtins.ListSerializer
import models.Task
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import models.TaskDTO
import models.toDTO
import models.toTask

const val SERVER_URL = "http://0.0.0.0:3000"

object APIService {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun scrapeTasks(url: String, dateIndex: Int, assignmentIndex: Int, courseName: String): List<Task> {
        val response: HttpResponse = client.get("$SERVER_URL/scrape") {
            parameter("website_url", url)
            parameter("date_index", dateIndex)
            parameter("assignment_index", assignmentIndex)
            parameter("course_name", courseName)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Failed to scrape tasks: ${response.status.description}")
        }

        val tasksDTO: List<TaskDTO> = json.decodeFromString(response.body())
        return tasksDTO.map { it.toTask() }
    }

    suspend fun createOrUpdateTask(task: Task): String {
        val response: HttpResponse = client.post("$SERVER_URL/task") {
            contentType(ContentType.Application.Json)
            setBody(task.toDTO())
        }

        if (!response.status.isSuccess()) {
            throw Exception("Failed to create or update task: ${response.status.description}")
        }

        return response.bodyAsText()
    }

    suspend fun deleteTask(task: Task): String {
        val response: HttpResponse = client.delete("$SERVER_URL/task") {
            contentType(ContentType.Application.Json)
            setBody(task.toDTO())
        }

        if (!response.status.isSuccess()) {
            throw Exception("Failed to delete task: ${response.status.description}")
        }

        return response.bodyAsText()
    }

    suspend fun getAllTasks(): List<Task> {
        val response: HttpResponse = client.get("$SERVER_URL/task")

        if (!response.status.isSuccess()) {
            throw Exception("Failed to get all tasks: ${response.status.description}")
        }

        val tasksDTO: List<TaskDTO> = json.decodeFromString(response.body())
        return tasksDTO.map { it.toTask() }
    }

    suspend fun getTasksFromOutline(courseName: String, courseOutline: String, openAIAPIKey: String): List<Task> {
        val response: HttpResponse = client.get("$SERVER_URL/generate-tasks-from-outline") {
            parameter("course_name", courseName)
            parameter("course_outline", courseOutline)
            header("Authorization", "Bearer $openAIAPIKey")
        }

        if (!response.status.isSuccess()) {
            throw Exception("Failed to generate tasks from outline: ${response.status.description}")
        }

        val tasksDTO: List<TaskDTO> = json.decodeFromString(response.body())
        return tasksDTO.map { it.toTask() }
    }

    suspend fun createBulkTasks(tasks: List<Task>): List<Task> {
        val tasksDTO = tasks.map { it.toDTO() }
        val response: HttpResponse = client.post("$SERVER_URL/tasks/bulk") {
            contentType(ContentType.Application.Json)
            setBody(tasksDTO)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Failed to add bulk tasks: ${response.status.description}")
        }

        val updatedTasksDTO: List<TaskDTO> = json.decodeFromString(response.body())
        return updatedTasksDTO.map { it.toTask() }
    }

    suspend fun exportTasksToICS(): String {
        val response: HttpResponse = client.get("$SERVER_URL/export-ics")

        if (!response.status.isSuccess()) {
            throw Exception("Failed to export tasks to ICS: ${response.status.description}")
        }

        return response.bodyAsText()
    }
}
