package io.taptap.stupidenglish

import androidx.annotation.DrawableRes

object NavigationKeys {

    object Arg {
        const val WORDS_ID = "words_id"
        const val SENTENCE_WORDS_ID = "sentence_words_id"
    }

    object Route {
        const val SE_MAIN = "stupid_english_main"

        const val SE_WORDS = "stupid_english_words"
        const val SENTENCES = "stupid_english_sentences"
        const val SE_SENTENCES = "$SENTENCES?${Arg.WORDS_ID}={${Arg.WORDS_ID}}"

        const val SE_ADD_WORD = "stupid_english_add_word"
        const val ADD_SENTENCE = "stupid_english_add_sentences"
        const val SE_ADD_SENTENCE = "$ADD_SENTENCE/{${Arg.SENTENCE_WORDS_ID}}"
//        const val REMEMBER = "stupid_english_remember"
//        const val SE_REMEMBER = "$REMEMBER/{${Arg.WORDS_ID}}"

        const val SE_IMPORT_WORDS = "stupid_english_import_words"
        const val SE_IMPORT_WORDS_TUTORIAL = "stupid_english_import_words_tutorial"
        const val SE_PROFILE = "stupid_english_profile"
        const val SE_AUTH = "stupid_english_auth"
        const val SE_TERMS = "stupid_english_terms"
        const val SE_SPLASH = "stupid_english_splash"
    }

    enum class BottomNavigationScreen(
        val route: String,
        val title: Int,
        @DrawableRes val icon: Int
    ) {
        SE_WORDS(
            route = Route.SE_WORDS,
            title = R.string.main_bottom_bar_words_title,
            icon = R.drawable.ic_head_icon
        ),
        SE_SENTENCES(
            route = Route.SE_SENTENCES,
            title = R.string.main_bottom_bar_sentences_title,
            icon = R.drawable.ic_cloud_icon
        )
    }
}
