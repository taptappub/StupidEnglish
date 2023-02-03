package io.taptap.stupidenglish.base.logic.sources.words.write

import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction

interface IWriteWordsDataSource {
    suspend fun saveWord(word: String, description: String, groupsIds: List<Long>): Reaction<Long>
    suspend fun saveWords(words: List<Word>): Reaction<Unit>
    suspend fun deleteWord(id: Long): Reaction<Unit>
    suspend fun deleteWords(list: List<Long>): Reaction<Unit>
}