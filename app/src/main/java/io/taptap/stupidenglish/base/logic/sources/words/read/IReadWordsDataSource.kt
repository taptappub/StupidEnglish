package io.taptap.stupidenglish.base.logic.sources.words.read

import io.taptap.stupidenglish.base.model.Word
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.flow.Flow
import taptap.pub.Reaction

interface IReadWordsDataSource {
    suspend fun getWordList(groupId: Long = NoGroup.id): Reaction<List<Word>>
    suspend fun getWordsCountInGroup(groupId: Long = NoGroup.id): Reaction<Int>
    suspend fun observeWordList(groupId: Long = NoGroup.id): Reaction<Flow<List<Word>>>
    suspend fun getWordsById(wordsIds: List<Long>): Reaction<List<Word>>
}