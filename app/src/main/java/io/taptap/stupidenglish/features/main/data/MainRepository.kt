package io.taptap.stupidenglish.features.main.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val wordDao: WordDao
) {

    suspend fun getWordsCount(): Reaction<Int> = Reaction.on {
        wordDao.getWords().size
    }
}
