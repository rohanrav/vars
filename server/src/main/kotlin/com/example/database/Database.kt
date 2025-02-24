package com.example.database

import com.example.models.Task
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Database {
    private var URL = "jdbc:sqlite:vars.db"
    private var connection: Connection? = null
    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    fun initDatabase(url: String = URL) {
        URL = url
        try {
            connection = DriverManager.getConnection(URL)
            setupDatabase()
            println("Connection to SQLite has been established.")
            println("Using Database URL: $URL")
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    fun closeConnection() {
        try {
            connection?.close()
            println("Database connection closed.")
        } catch (e: SQLException) {
            println("Error closing database: ${e.message}")
        }
    }

    fun getConnection(): Connection? {
        return connection
    }

    private fun setupDatabase() {
        val createTableSQL = """
            CREATE TABLE IF NOT EXISTS tasks (
                id TEXT PRIMARY KEY,
                taskName TEXT,
                dueDate TEXT,
                course TEXT,
                weight REAL,
                score REAL
            );
        """.trimIndent()

        try {
            connection?.createStatement()?.apply {
                executeUpdate(createTableSQL)
            }
        } catch (ex: SQLException) {
            println(ex.message)
        }
    }

    fun resetDatabase() {
        val dropTableSQL = "DROP TABLE IF EXISTS tasks;"
        try {
            connection?.createStatement()?.apply {
                executeUpdate(dropTableSQL)
                println("Database tables dropped successfully.")
            }
        } catch (ex: SQLException) {
            println("Error dropping tables: ${ex.message}")
        }
    }


    fun insertTasks(tasks: List<Task>) {
        val insertTaskSQL = """
        INSERT INTO tasks(id, taskName, dueDate, course, weight, score) 
        VALUES (?, ?, ?, ?, ?, ?)
    """.trimIndent()

        try {
            connection?.prepareStatement(insertTaskSQL)?.use { statement ->
                for (task in tasks) {
                    statement.setString(1, task.id.toString())
                    statement.setString(2, task.taskName)
                    statement.setString(3, format.format(task.dueDate))
                    statement.setString(4, task.course)
                    statement.setDouble(5, task.weight)
                    statement.setDouble(6, task.score)
                    statement.addBatch()
                }
                statement.executeBatch()
            }
        } catch (ex: SQLException) {
            println(ex.message)
        }
    }


    fun updateTask(task: Task) {
        val updateTaskSQL = """
        UPDATE tasks
        SET taskName = ?, dueDate = ?, course = ?, weight = ?, score = ?
        WHERE id = ?
    """.trimIndent()

        try {
            connection?.prepareStatement(updateTaskSQL)?.apply {
                setString(1, task.taskName)
                setString(2, format.format(task.dueDate))
                setString(3, task.course)
                setDouble(4, task.weight)
                setDouble(5, task.score)
                setString(6, task.id.toString())

                executeUpdate()
            }
        } catch (ex: SQLException) {
            println(ex.message)
        }
    }

    fun deleteTask(task: Task) {
        val deleteTaskSQL = """
            DELETE FROM tasks
            WHERE id = ?
        """.trimIndent()

        try {
            connection?.prepareStatement(deleteTaskSQL)?.apply {
                setString(1, task.id.toString())
                executeUpdate()
            }
        } catch (ex: SQLException) {
            println(ex.message)
        }
    }

    fun getTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val getTasksSQL = """
            SELECT * FROM tasks
        """.trimIndent()

        try {
            val statement = connection?.createStatement()
            val resultSet = statement?.executeQuery(getTasksSQL)
            while (resultSet?.next() == true) {
                try {
                    tasks.add(Task(
                        id = UUID.fromString(resultSet.getString("id")),
                        taskName = resultSet.getString("taskName"),
                        dueDate = format.parse(resultSet.getString("dueDate")),
                        course = resultSet.getString("course"),
                        weight = resultSet.getDouble("weight"),
                        score = resultSet.getDouble("score")
                    ))
                } catch (ex: ParseException) {
                    println("Error Parsing Date From Database: ${ex.message}")
                }
            }
        } catch (ex: SQLException) {
            println(ex.message)
        }
        return tasks
    }

    fun getTaskById(id: UUID): Task? {
        val getTaskSQL = """
            SELECT * FROM tasks WHERE id = ?
        """.trimIndent()

        try {
            connection?.prepareStatement(getTaskSQL)?.let { statement ->
                statement.setString(1, id.toString())
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    return Task(
                        id = UUID.fromString(resultSet.getString("id")),
                        taskName = resultSet.getString("taskName"),
                        dueDate = format.parse(resultSet.getString("dueDate")),
                        course = resultSet.getString("course"),
                        weight = resultSet.getDouble("weight"),
                        score = resultSet.getDouble("score")
                    )
                }
            }
        } catch (ex: SQLException) {
            println("Error fetching task: ${ex.message}")
        }

        return null
    }

}
