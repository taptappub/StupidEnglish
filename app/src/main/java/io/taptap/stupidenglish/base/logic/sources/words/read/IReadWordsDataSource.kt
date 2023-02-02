package io.taptap.stupidenglish.base.logic.sources.words.read

import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction

interface IReadWordsDataSource {
    suspend fun getWordsByGroupId(groupId: Long): Reaction<List<Word>>
    suspend fun getWordsCountInGroup(groupId: Long): Reaction<Int>
    suspend fun getWordsById(wordsIds: List<Long>): Reaction<List<Word>>
}