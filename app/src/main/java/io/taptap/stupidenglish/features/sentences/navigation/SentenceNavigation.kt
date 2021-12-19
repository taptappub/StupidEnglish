package io.taptap.stupidenglish.features.sentences.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class SentenceNavigation(
    val wordsIds: List<Long>
): Parcelable

class SentenceNavigationNavType : NavType<SentenceNavigation>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): SentenceNavigation? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): SentenceNavigation {
        return Gson().fromJson(value, SentenceNavigation::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: SentenceNavigation) {
        bundle.putParcelable(key, value)
    }
}