package io.taptap.stupidenglish.features.addsentence.data

import com.google.gson.Gson
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.mapper.toWord
import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSentenceRepository @Inject constructor(
    private val wordDao: WordDao
) {

    suspend fun saveSentence(sentence: String, wordIds: List<Long>): Reaction<Long> = Reaction.on {
        val wordIdsString = Gson().toJson(wordIds)
        wordDao.insertSentence(SentenceDto(
            sentence = sentence,
            words = wordIdsString
        ))
    }

    suspend fun getWordsById(wordsIds: List<Long>): Reaction<List<Word>> = Reaction.on {
        wordsIds.map {
            wordDao.getWordDto(it)?.toWord()
                ?: error("There is no word with id = $it")
        }
    }
}
