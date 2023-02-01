package io.taptap.stupidenglish.base.logic.randomwords

import io.taptap.stupidenglish.base.getRandom
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.convertGroupsIds
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.model.Word
import io.taptap.uikit.group.NoGroup
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RandomWordsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IRandomWordsDataSource {

    override suspend fun getRandomWords(maxCount: Int, groupId: Long): Reaction<List<Word>> = Reaction.on {
        val words = wordDao.getWords()
        val wordsByGroup = if (groupId == NoGroup.id) {
            words
        } else {
            words.filter {
                it.groupsIds.convertGroupsIds().contains(groupId)
            }
        }
        val realCount = when {
            wordsByGroup.size in 1 until maxCount -> wordsByGroup.size
            wordsByGroup.size >= maxCount -> maxCount
            else -> error("words.size is 0")
        }
        wordsByGroup.getRandom(realCount).toWords()
    }
}
