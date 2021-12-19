package io.taptap.stupidenglish.features.addword.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWordRepository @Inject constructor(
    private val wordDao: WordDao
) {

    suspend fun saveWord(word: String, description: String): Reaction<Long> = Reaction.on {
        wordDao.insertWord(WordDto(word = word, description = description))
    }
}