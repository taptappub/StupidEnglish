package io.taptap.stupidenglish.features.main.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.logic.mapper.toWord
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import io.taptap.stupidenglish.base.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainListRepository @Inject constructor(
    randomWordsDataSource: RandomWordsDataSource,
    private val wordDao: WordDao
) : IRandomWordsDataSource by randomWordsDataSource {

    suspend fun getWordList(): Reaction<Flow<List<Word>>> = Reaction.on {
        wordDao.observeWords()
            .map { wordDtos ->
                wordDtos.toWords()
            }
    }
}
