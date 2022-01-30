package io.taptap.stupidenglish.base.logic.prefs

import android.content.Context
import io.taptap.core.PreferenceProperty
import javax.inject.Inject

private const val PREFERENCES_NAME = "storage"

open class Settings @Inject constructor(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var isFirstStart by publicProperty(true, "is_first_start")
    var isSentenceMotivationShown by publicProperty(false, "is_sentence_motivation_shown")
    var isShareMotivationShown by publicProperty(false, "is_share_motivation_shown")

    private fun <T> publicProperty(default: T, key: String) =
        PreferenceProperty(default, key, sharedPreferences)
}