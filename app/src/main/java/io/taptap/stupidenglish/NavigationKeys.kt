package io.taptap.stupidenglish

import io.taptap.stupidenglish.NavigationKeys.Arg.WORD_ID

object NavigationKeys {

    object Arg {
        const val WORD_ID = "word_id"
    }

    object Route {
        const val SE_LIST = "stupid_english_list"
        const val SE_ADD_WORD = "stupid_english_add_word"
        const val SE_WORD_DETAILS = "$SE_LIST/{$WORD_ID}"
    }
}