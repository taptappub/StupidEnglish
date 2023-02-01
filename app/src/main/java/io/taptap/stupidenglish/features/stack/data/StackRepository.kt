package io.taptap.stupidenglish.features.stack.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.sources.words.read.IReadWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.read.ReadWordsDataSource
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StackRepository @Inject constructor(
    private val readReadWordsDataSource: ReadWordsDataSource,
    private val wordDao: WordDao
) : IReadWordsDataSource by readReadWordsDataSource {

    suspend fun rememberWord(wordId: Long): Reaction<Unit> = Reaction.on {
        val wordDto = wordDao.getWordDto(wordId)
        val newWordDto = wordDto?.copy(points = wordDto.points + 1)
        newWordDto?.let {
            wordDao.updateWord(it)
        }
    }
}
