package io.taptap.stupidenglish.base.logic.sources.words.write

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WriteWordsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IWriteWordsDataSource {

    override suspend fun saveWord(
        word: String,
        description: String,
        groupsIds: List<Long>
    ): Reaction<Long> = Reaction.on {
        wordDao.insertWord(
            WordDto(
                word = word,
                description = description,
                points = 0,
                groupsIds = groupsIds.joinToString(",")
            )
        )
    }

    override suspend fun saveWords(words: List<Word>): Reaction<Unit> = Reaction.on {
        val wordsDto = words.map {
            WordDto(
                word = it.word,
                description = it.description,
                points = it.points,
                groupsIds = it.groupsIds.joinToString(",")
            )
        }
        wordDao.insertWords(wordsDto)
    }

    override suspend fun deleteWord(id: Long): Reaction<Unit> = Reaction.on {
        wordDao.deleteWord(id)
    }

    override suspend fun deleteWords(list: List<Long>): Reaction<Unit> = Reaction.on {
        wordDao.deleteWords(list)
    }
}