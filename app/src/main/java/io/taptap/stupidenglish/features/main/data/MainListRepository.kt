package io.taptap.stupidenglish.features.main.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainListRepository @Inject constructor(
    randomWordsDataSource: RandomWordsDataSource,
    private val wordDao : WordDao
) : IRandomWordsDataSource by randomWordsDataSource {

    suspend fun getWordList(): Reaction<List<Word>> {
        return Reaction.on {
            listOf(
                Word(0, "Privet", "Ass"),
                Word(1, "Salut", "Dick"),
                Word(2, "Flibustiera", "Cunt")
            )
        }
    }
}