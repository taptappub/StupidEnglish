package io.taptap.stupidenglish.base.logic.sources.words.write

import io.taptap.stupidenglish.base.model.GroupWithWords
import io.taptap.stupidenglish.base.model.WordWithGroups
import taptap.pub.Reaction

interface IWriteWordsDataSource {
    suspend fun saveWord(word: String, description: String, groupsIds: List<Long>): Reaction<Unit>
    suspend fun saveWords(words: List<WordWithGroups>): Reaction<Unit>
    suspend fun deleteWord(id: Long): Reaction<Unit>
    suspend fun deleteWords(list: List<Long>): Reaction<Unit>
}