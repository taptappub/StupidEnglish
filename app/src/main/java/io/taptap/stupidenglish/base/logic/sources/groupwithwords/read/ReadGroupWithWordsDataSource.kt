package io.taptap.stupidenglish.base.logic.sources.groupwithwords.read

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.toGroupWithWords
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.model.GroupWithWords
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadGroupWithWordsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IReadGroupWithWordsDataSource {

    override suspend fun observeGroupWithWords(groupId: Long): Reaction<Flow<GroupWithWords>> = Reaction.on {
        if (groupId == NoGroup.id) {
            wordDao.observeWords()
                .map {
                    GroupWithWords(
                        group = null,
                        words = it.toWords()
                    )
                }
        } else {
            wordDao.observeGroupWithWords(groupId)
                .map {
                    it.toGroupWithWords()
                }
        }
    }

    override suspend fun getGroupWithWords(groupId: Long): Reaction<GroupWithWords> = Reaction.on {
        if (groupId == NoGroup.id) {
            GroupWithWords(
                group = null,
                words = wordDao.getWords().toWords()
            )
        } else {
            wordDao.getGroupWithWords(groupId)
                .toGroupWithWords()
        }
    }

    override suspend fun getWordsCountInGroup(groupId: Long): Reaction<Int> = Reaction.on {
        wordDao.getWordsCountInGroup(groupId)
    }
}
