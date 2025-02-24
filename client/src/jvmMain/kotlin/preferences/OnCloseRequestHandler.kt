package preferences

import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.WindowState
import kotlin.system.exitProcess

fun onCloseRequestHandler(windowState: WindowState, currentView: MutableState<String>) {
    val preferences = PreferencesManager
    val appPreferences = AppPreferences(
        windowState.size.width.value.toInt(),
        windowState.size.height.value.toInt(),
        windowState.position.x.value.toInt(),
        windowState.position.y.value.toInt(),
        currentView.value,
        preferences.loadPreferences().openAIAPIKey,
    )
    preferences.savePreferences(appPreferences)
    exitProcess(0)
}

fun saveOpenAIAPIKey(APIKey: String?) {
    val preferences = PreferencesManager
    val currentPreferences = preferences.loadPreferences()
    val updatedPreferences = AppPreferences(
        currentPreferences.windowWidth,
        currentPreferences.windowHeight,
        currentPreferences.windowX,
        currentPreferences.windowY,
        currentPreferences.viewType,
        APIKey
    )
    preferences.savePreferences(updatedPreferences)
}
