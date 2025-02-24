package preferences

data class AppPreferences(
    val windowWidth: Int,
    val windowHeight: Int,
    val windowX: Int,
    val windowY: Int,
    val viewType: String,
    val openAIAPIKey: String?
)
