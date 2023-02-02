package io.taptap.stupidenglish.base.logic.sources.groups.write

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WriteGroupsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IWriteGroupsDataSource {

    override suspend fun saveGroup(group: String): Reaction<Long> = Reaction.on {
        wordDao.insertGroup(GroupDto(name = group))
    }

    override suspend fun removeGroups(groupsIds: List<Long>): Reaction<Unit> = Reaction.on {
        wordDao.deleteGroups(groupsIds)
    }
}
