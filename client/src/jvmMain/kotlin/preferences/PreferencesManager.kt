package preferences

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object PreferencesManager {
    private const val PREFERENCES_FILE_NAME = "app_preferences.json"
    private val objectMapper = jacksonObjectMapper()
    private val preferencesFilePath: Path = Paths.get(System.getProperty("user.home"), PREFERENCES_FILE_NAME)

    fun savePreferences(preferences: AppPreferences) {
        try {
            val json = objectMapper.writeValueAsString(preferences)
            Files.write(preferencesFilePath, json.toByteArray())
        } catch (e: Exception) {
            println("SAVE ERROR")
        }
    }

    fun loadPreferences(): AppPreferences {
        return try {
            val json = String(Files.readAllBytes(preferencesFilePath))
            objectMapper.readValue(json, AppPreferences::class.java)
        } catch (e: Exception) {
            AppPreferences(windowWidth = 800, windowHeight = 600, windowX = 0, windowY = 0, viewType = "COURSEVIEW", openAIAPIKey = null)
        }
    }
}