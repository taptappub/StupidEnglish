package io.taptap.stupidenglish.features.sentences.data

import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import io.taptap.stupidenglish.base.model.Sentence
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SentencesListRepository @Inject constructor(
    randomWordsDataSource: RandomWordsDataSource
) : IRandomWordsDataSource by randomWordsDataSource {

    fun getSentenceList(): Reaction<List<Sentence>> {
        return Reaction.on {
            listOf(
                Sentence(0, "The first awesome sentence"),
                Sentence(1, "The second awesome sentence")
            )
        }
    }
}