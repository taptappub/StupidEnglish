package io.taptap.stupidenglish.base.logic.randomwords

import android.util.Log
import io.taptap.stupidenglish.base.getRandom
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.logic.mapper.toWord
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RandomWordsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IRandomWordsDataSource {

    override suspend fun getRandomWords(count: Int): Reaction<List<Word>> = Reaction.on {
        val words = wordDao.getWords()
        Log.d("TAGGGG", "words = $words")
        if (words.size >= count) {
            Log.d("TAGGGG", "more")
            words.getRandom(count).toWords()
        } else {
            throw IllegalStateException("Cant find $count items")
        }
    }
}
