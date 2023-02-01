package io.taptap.stupidenglish.features.addsentence.data

import com.google.gson.Gson
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.mapper.toWord
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.read.IReadWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.read.ReadWordsDataSource
import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSentenceRepository @Inject constructor(
    private val randomWordsDataSource: IRandomWordsDataSource,
    private val readWordsDataSource: IReadWordsDataSource,
    private val wordDao: WordDao
) : IRandomWordsDataSource by randomWordsDataSource,
    IReadWordsDataSource by readWordsDataSource{

    suspend fun saveSentence(sentence: String, wordIds: List<Long>): Reaction<Long> = Reaction.on {
        val wordIdsString = Gson().toJson(wordIds)
        wordDao.insertSentence(SentenceDto(
            sentence = sentence,
            words = wordIdsString
        ))
    }
}
