package io.taptap.uikit.prefs

import android.content.Context
import io.taptap.core.PreferenceProperty

private const val PREFERENCES_NAME = "uikit_storage"

//todo переделай на DataStore
open class UiKitSettings(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var theme by publicProperty(ThemeType.LIGHT.value, "sl_ui_theme")

    private fun <T> publicProperty(default: T, key: String) =
        PreferenceProperty(default, key, sharedPreferences)
}

enum class ThemeType(val value: String) {
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun getByValue(value: String): ThemeType = values().find { it.value == value }
            ?: error("ThemeType with value $value not found")
    }
}