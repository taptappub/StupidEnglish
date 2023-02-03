package io.taptap.stupidenglish.base.logic.randomwords

import io.taptap.stupidenglish.base.getRandom
import io.taptap.stupidenglish.base.logic.sources.words.read.IReadWordsDataSource
import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import taptap.pub.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RandomWordsDataSource @Inject constructor(
    private val readWordsDataSource: IReadWordsDataSource
) : IRandomWordsDataSource,
    IReadWordsDataSource by readWordsDataSource {

    override suspend fun getRandomWords(maxCount: Int, groupId: Long): Reaction<List<Word>> {
        return getWordList(groupId)
            .map { wordsByGroup ->
                val realCount = when {
                    wordsByGroup.size in 1 until maxCount -> wordsByGroup.size
                    wordsByGroup.size >= maxCount -> maxCount
                    else -> error("words.size is 0")
                }

                wordsByGroup.getRandom(realCount)
            }
    }
}
