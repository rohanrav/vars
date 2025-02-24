package com.example

import com.example.database.Database
import com.example.import_task_automation.format
import com.example.models.Task
import com.example.models.TaskDTO
import com.example.models.toDTO
import com.example.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.*

class ApplicationTest {
    // Test Task for Tests
    private val newTask1 = Task(
        id = UUID.randomUUID(),
        taskName = "Test Task 1",
        dueDate = Date(),
        course = "Test Course 1",
        weight = 30.0,
        score = 90.0
    )

    private val newTask2 = Task(
        id = UUID.randomUUID(),
        taskName = "Test Task 2",
        dueDate = Date(),
        course = "Test Course 2",
        weight = 20.0,
        score = 35.0
    )

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @BeforeTest
    fun resetDatabase() {
        val testDbUrl = "jdbc:sqlite:file::memory:?cache=shared"
        Database.initDatabase(testDbUrl)
        println("Using in-memory database for testing: $testDbUrl")
    }

    @AfterTest
    fun closeDatabase() {
        Database.closeConnection()
    }

    @Test
    fun testRoot() = testApplication {
        application {
            // Configure server routes
            configureSerialization()
            configureHTTP()
            configureRouting()

            println("\nTest Root:\n")
        }

        val httpClient = createClient {
            install(ContentNegotiation) {
                json(json)
            }
        }

        // Unit Test
        httpClient.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun testCreateTask() = testApplication {
        application {
            configureSerialization()
            configureHTTP()
            configureRouting()

            println("\nTest Create Task:\n")
        }

        val httpClient = createClient {
            install(ContentNegotiation) {
                json(json)
            }
        }

        httpClient.post("/task") {
            contentType(ContentType.Application.Json)
            setBody(newTask1.toDTO())
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertEquals("Task created successfully", bodyAsText())

            val createdTask = Database.getTaskById(newTask1.id)
            assertEquals(newTask1.id, createdTask?.id)
        }
    }

    @Test
    fun testUpdateTask() = testApplication {
        application {
            configureSerialization()
            configureHTTP()
            configureRouting()

            println("\nTest Update Task:\n")
        }

        val httpClient = createClient {
            install(ContentNegotiation) {
                json(json)
            }
        }

        // Insert Task to update
        Database.insertTasks(listOf(newTask2))

        httpClient.post("/task") {
            contentType(ContentType.Application.Json)
            setBody(newTask2.toDTO().copy(course = "Updated Course"))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Task updated successfully", bodyAsText())

            val updatedTask = Database.getTaskById(newTask2.id)
            assertEquals("Updated Course", updatedTask?.course)
        }
    }

    @Test
    fun testGetAllTasks() = testApplication {
        application {
            configureSerialization()
            configureHTTP()
            configureRouting()

            println("\nTest Get All Tasks:\n")
        }

        val httpClient = createClient {
            install(ContentNegotiation) {
                json(json)
            }
        }

        // Insert tasks
        Database.insertTasks(listOf(
            newTask1,
            newTask2
        ))

        httpClient.get("/task").apply {
            assertEquals(HttpStatusCode.OK, status)

            val allTasks = Database.getTasks()
            assertEquals(
                listOf(newTask1, newTask2),
                allTasks
            )
        }
    }

    @Test
    fun testDeleteTask() = testApplication {
        application {
            configureSerialization()
            configureHTTP()
            configureRouting()

            println("\nTest Delete Task:\n")
        }

        val httpClient = createClient {
            install(ContentNegotiation) {
                json(json)
            }
        }

        // Insert Tasks to delete
        Database.insertTasks(listOf(newTask1))

        httpClient.delete("/task") {
            contentType(ContentType.Application.Json)
            setBody(newTask1.toDTO())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Task deleted successfully", bodyAsText())

            assertEquals(0, Database.getTasks().size)
        }
    }

    @Test
    fun testWebScraping() = testApplication {
        application {
            configureSerialization()
            configureHTTP()
            configureRouting()

            println("\nTest Web Scraping Tasks:\n")
        }

        val httpClient = createClient {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val webScrapeURL = "https://student.cs.uwaterloo.ca/~cs346/1239/schedule/index.html"

        httpClient.get("/scrape") {
            parameter("website_url", webScrapeURL)
            parameter("date_index", 0)
            parameter("assignment_index", 4)
            parameter("course_name", "CS 346")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)

            val tasksDTO: List<TaskDTO> = json.decodeFromString(bodyAsText())
            assertEquals(6, tasksDTO.size)
        }
    }

    @Test
    fun testAddBulkTasks() = testApplication {
        application {
            configureSerialization()
            configureHTTP()
            configureRouting()

            println("\nTest Add Bulk Tasks:\n")
        }

        val httpClient = createClient {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val tasksToAdd = listOf(newTask1, newTask2).map { it.toDTO() }

        httpClient.post("/tasks/bulk") {
            contentType(ContentType.Application.Json)
            setBody(tasksToAdd)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)

            val tasksDTO: List<TaskDTO> = json.decodeFromString(bodyAsText())
            assertEquals(tasksToAdd.size, tasksDTO.size)
        }
    }

    @Test
    fun testExportTasksToICS() = testApplication {
        application {
            configureSerialization()
            configureHTTP()
            configureRouting()

            println("\nTest Export Tasks to ICS:\n")
        }

        val httpClient = createClient {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val tasksToAdd = listOf(
            newTask1.copy(
                dueDate = format.parse("2023-12-04T00:15:02.276Z")
            ),
            newTask2.copy(
                dueDate = format.parse("2023-12-04T00:15:02.276Z")
            )
        )

        // Insert tasks to export
        Database.insertTasks(tasksToAdd)

        httpClient.get("/export-ics").apply {
            assertEquals(HttpStatusCode.OK, status)

            val responseStr = bodyAsText()
            assertTrue(responseStr.contains("BEGIN:VCALENDAR"))
            assertTrue(responseStr.contains("END:VCALENDAR"))
        }
    }
}
