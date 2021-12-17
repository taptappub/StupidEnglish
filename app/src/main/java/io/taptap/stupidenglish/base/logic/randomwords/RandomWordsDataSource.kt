package io.taptap.stupidenglish.base.logic.randomwords

import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RandomWordsDataSource @Inject constructor() : IRandomWordsDataSource {

    override suspend fun getRandomWords(count: Int): Reaction<List<Word>> {
        return Reaction.on {
            listOf(
                Word(0, "Privet", "Ass"),
                Word(1, "Salut", "Dick"),
                Word(2, "Flibustiera", "Cunt")
            )
        }
    }
}