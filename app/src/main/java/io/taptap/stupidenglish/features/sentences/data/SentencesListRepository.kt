package io.taptap.stupidenglish.features.sentences.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.toSentences
import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import io.taptap.stupidenglish.base.model.Sentence
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SentencesListRepository @Inject constructor(
    randomWordsDataSource: RandomWordsDataSource,
    private val wordDao: WordDao,
    private val settings: Settings
) : IRandomWordsDataSource by randomWordsDataSource {

    var isShareMotivationShown: Boolean
        get() {
            return settings.isShareMotivationShown
        }
        set(value) {
            settings.isShareMotivationShown = value
        }

    fun observeSentenceList(): Reaction<Flow<List<Sentence>>> = Reaction.on {
        wordDao.observeSentences()
            .map { sentenceDtos ->
                sentenceDtos.toSentences()
            }
    }

    suspend fun deleteSentence(id: Long): Reaction<Unit> = Reaction.on {
        wordDao.deleteSentence(id)
    }
}
