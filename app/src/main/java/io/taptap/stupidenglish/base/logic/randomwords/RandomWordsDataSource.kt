package io.taptap.stupidenglish.base.logic.randomwords

import io.taptap.stupidenglish.base.getRandom
import io.taptap.stupidenglish.base.logic.sources.groupwithwords.read.IReadGroupWithWordsDataSource
import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import taptap.pub.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RandomWordsDataSource @Inject constructor(
    private val readGroupWithWordsDataSource: IReadGroupWithWordsDataSource
) : IRandomWordsDataSource,
    IReadGroupWithWordsDataSource by readGroupWithWordsDataSource {

    override suspend fun getRandomWords(maxCount: Int, groupId: Long): Reaction<List<Word>> {
        return getGroupWithWords(groupId)
            .map { group ->
                val words = group.words
                val realCount = when {
                    words.size in 1 until maxCount -> words.size
                    words.size >= maxCount -> maxCount
                    else -> error("words.size is 0")
                }
                words.getRandom(realCount)
            }
    }
}
