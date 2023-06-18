package io.taptap.stupidenglish.base.logic.sources.groups.write

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WriteGroupsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IWriteGroupsDataSource {
    override suspend fun rearrangeGroups(fromGroupId: Long, toGroupId: Long): Reaction<Unit> = Reaction.on {
        wordDao.rearrangeGroups(fromGroupId, toGroupId)
    }

    override suspend fun saveGroup(groupName: String): Reaction<Long> = Reaction.on {
        wordDao.insertGroup(groupName)
    }

    override suspend fun removeGroups(groupsIds: List<Long>): Reaction<Unit> = Reaction.on {
        wordDao.deleteGroups(groupsIds)
    }
}
