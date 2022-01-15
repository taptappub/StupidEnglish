package io.taptap.stupidenglish.features.stack.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.toWord
import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StackRepository @Inject constructor(
    private val wordDao: WordDao
) {
    suspend fun getWordsById(wordsIds: List<Long>): Reaction<List<Word>> = Reaction.on {
        wordsIds.map {
            wordDao.getWordDto(it)?.toWord()
                ?: throw IllegalStateException("There is no word with id = $it")
        }
    }

    suspend fun rememberWord(wordId: Long): Reaction<List<Word>> = Reaction.on {
        emptyList()
    }
}
