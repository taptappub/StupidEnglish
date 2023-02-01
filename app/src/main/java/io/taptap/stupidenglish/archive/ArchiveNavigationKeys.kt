package io.taptap.stupidenglish.archive

import androidx.annotation.DrawableRes
import io.taptap.stupidenglish.R

object ArchiveNavigationKeys {

    object Arg {
        const val WORDS_IDS = "words_ids"
    }

    object Route {
        const val REMEMBER = "stupid_english_remember"
        const val SE_REMEMBER = "$REMEMBER/{${Arg.WORDS_IDS}}"

        const val SENTENCES = "stupid_english_sentences"
        const val SE_SENTENCES = "$SENTENCES?${Arg.WORDS_IDS}={${Arg.WORDS_IDS}}"
    }

    enum class BottomNavigationScreen(
        val route: String,
        val title: Int,
        @DrawableRes val icon: Int
    ) {
        SE_SENTENCES(
            route = Route.SE_SENTENCES,
            title = R.string.main_bottom_bar_sentences_title,
            icon = R.drawable.ic_cloud_icon
        )
    }
}
