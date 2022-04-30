package io.taptap.stupidenglish.base.logic.sources.words

import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction

interface IWordsDataSource {
    suspend fun saveWord(word: String, description: String, groupsIds: List<Long>): Reaction<Long>
    suspend fun saveWords(words: List<Word>): Reaction<Unit>
}