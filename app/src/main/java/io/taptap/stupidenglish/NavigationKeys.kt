package io.taptap.stupidenglish

import io.taptap.stupidenglish.NavigationKeys.Arg.SENTENCE_WORDS_ID

object NavigationKeys {

    object Arg {
        const val WORD_ID = "word_id"
        const val SENTENCE_WORDS_ID = "sentence_words_id"
    }

    object Route {
        const val SE_LIST = "stupid_english_list"
        const val SE_ADD_WORD = "stupid_english_add_word"
        const val SE_SENTENCES_LIST = "stupid_english_sentences_list"
        const val SE_ADD_SENTENCE = "{$SE_SENTENCES_LIST}/{$SENTENCE_WORDS_ID}"
    }
}