package io.taptap.stupidenglish.base.logic.randomwords

import io.taptap.stupidenglish.base.getRandom
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RandomWordsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IRandomWordsDataSource {

    override suspend fun getRandomWords(maxCount: Int): Reaction<List<Word>> = Reaction.on {
        val words = wordDao.getWords()
        val realCount = when {
            words.size in 1 until maxCount -> words.size
            words.size >= maxCount -> maxCount
            else -> error("words.size is 0")
        }
        words.getRandom(realCount).toWords()
    }
}
