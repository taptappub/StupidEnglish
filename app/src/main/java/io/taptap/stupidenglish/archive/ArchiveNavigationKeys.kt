package io.taptap.stupidenglish.archive

import androidx.annotation.DrawableRes
import io.taptap.stupidenglish.R

object ArchiveNavigationKeys {

    object Arg {
        const val WORDS_IDS = "words_ids"
        const val SENTENCE_WORDS_ID = "sentence_words_id"
    }

    object Route {
        const val REMEMBER = "stupid_english_remember"
        const val SE_REMEMBER = "$REMEMBER/{${Arg.WORDS_IDS}}"

        const val SENTENCES = "stupid_english_sentences"
        const val SE_SENTENCES = "$SENTENCES?${Arg.WORDS_IDS}={${Arg.WORDS_IDS}}"
        const val ADD_SENTENCE = "stupid_english_add_sentences"
        const val SE_ADD_SENTENCE = "$ADD_SENTENCE/{${Arg.SENTENCE_WORDS_ID}}"
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
