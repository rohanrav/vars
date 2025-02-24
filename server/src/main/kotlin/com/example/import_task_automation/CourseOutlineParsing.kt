package com.example.import_task_automation

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import com.example.models.Task
import java.text.ParseException
import java.text.SimpleDateFormat

val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
const val OPENAI_API_URL = "https://api.openai.com/v1/chat/completions"

/**
 * Retrieves a list of tasks extracted from a course outline using the OpenAI API.
 *
 * The function takes a course outline as a string and a course name, sends it to the
 * OpenAI GPT 3.5 API, and returns a list of tasks.
 */
suspend fun getTasksFromCourseOutline(courseOutline: String, courseName: String, openAIAPIKey: String): List<Task> {
    var tasks: List<Task>

    // Build request payload JSON
    val payload = buildPayload(courseOutline, courseName)

    // Configure HTTP Client
    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
        }
        install(ContentNegotiation) {
            json()
        }
    }

    println("Fetching Results from OpenAI API With Payload: $payload")

    // Make API Request to OpenAI GPT-3.5
    val response: HttpResponse = client.post(OPENAI_API_URL) {
        headers {
            append(HttpHeaders.Authorization, "Bearer $openAIAPIKey")
        }
        contentType(ContentType.Application.Json)
        setBody(payload)
    }
    client.close()

    val responseBody = response.bodyAsText()
    if (response.status.value != 200) {
        throw Exception("Error Fetching Results From OpenAI: $responseBody")
    }

    // Parse Request Response
    val jsonElement = Json.parseToJsonElement(responseBody)
    val messageContent = jsonElement
        .jsonObject["choices"]?.jsonArray?.first()?.jsonObject
        ?.get("message")?.jsonObject
        ?.get("content")?.jsonPrimitive?.content

    if (messageContent == null) {
        throw Exception("Error parsing message content from response: $jsonElement")
    }

    // Parse message content as expected JSON response
    val tasksJsonElement = Json.parseToJsonElement(messageContent)
    val tasksArray = tasksJsonElement.jsonObject["tasks"]?.jsonArray

    println("RESPONSE: ${responseBody}")

    // Transform JSON array into list of Tasks
    tasks = tasksArray?.mapNotNull { t ->
        try {
            val taskJson = t.jsonObject
            Task(
                taskName = taskJson["taskName"]?.jsonPrimitive?.content ?: "",
                dueDate = format.parse(taskJson["dueDate"]?.jsonPrimitive?.content),
                course = taskJson["course"]?.jsonPrimitive?.content ?: "",
                weight = taskJson["weight"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            )
        } catch (e: ParseException) {
            println("Skipping task due to invalid date format: $t")
            null
        }
    } ?: emptyList()

    client.close()
    return tasks
}

fun buildPayload(courseOutline: String, courseName: String): JsonObject {
    return buildJsonObject {
        put("model", "gpt-3.5-turbo-16k")
        putJsonArray("messages") {
            addJsonObject {
                put("role", "system")
                put("content", "You will be provided with text from course outlines. Your task is to extract information about various academic tasks including assignments, midterms, finals, etc. For each task, provide a detailed task name, due date, course name, and the weight of the task towards the final grade. The response should be in JSON format, following this structure:\\n\\n{\\n  \\\"tasks\\\": [\\n    {\\n      \\\"taskName\\\": \\\"String\\\",\\n      \\\"dueDate\\\": \\\"Date in format yyyy-MM-dd'\\''T'\\''HH:mm:ss.SSS'\\''Z'\\''\\\",\\n      \\\"course\\\": \\\"String\\\",\\n      \\\"weight\\\": \\\"Double\\\"\\n    }\\n    // More tasks follow in the array\\n  ]\\n}\\n\\nIf a date does not exist for a particular task, use the date December 1st, 2023 formatted as specified. Please only include tasks that have all of the required information available. The date field is an exception.")
            }
            addJsonObject {
                put("role", "user")
                put("content", "Here is a course outline for the course \"${courseName}\":\n\nCourse Outline:\n\n${courseOutline}\n\nPlease convert the schedule into the specified JSON format.")
            }
        }
        put("temperature", 0.7)
        put("max_tokens", 7000)
        put("top_p", 1)
        put("frequency_penalty", 0.02)
        put("presence_penalty", 0)
    }
}