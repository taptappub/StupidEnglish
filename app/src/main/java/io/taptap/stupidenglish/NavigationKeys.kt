package io.taptap.stupidenglish

import io.taptap.stupidenglish.NavigationKeys.Arg.PAGE_ID
import io.taptap.stupidenglish.NavigationKeys.Arg.SENTENCE_WORDS_ID

object NavigationKeys {

    object Arg {
        const val WORDS_ID = "words_id"
        const val SENTENCE_WORDS_ID = "sentence_words_id"
        const val PAGE_ID = "page_id"
    }

    object Route {
        const val SE_LIST = "stupid_english_list"
        const val SE_ADD_WORD = "stupid_english_add_word"
        const val SE_SENTENCES_LIST = "sentences_list"
        const val SE_REMEMBER = "remember"
    }
}