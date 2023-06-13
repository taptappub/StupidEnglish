package io.taptap.stupidenglish.base.logic.sources.groups.read

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.toGroup
import io.taptap.stupidenglish.base.logic.mapper.toGroups
import io.taptap.stupidenglish.base.model.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadGroupsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IReadGroupsDataSource {

    override fun observeGroupList(): Flow<List<Group>> = wordDao.observeGroups()
        .map { groupDtos ->
            groupDtos.toGroups()
        }

    override suspend fun getGroupList(): Reaction<List<Group>> = Reaction.on {
        wordDao.getGroups()
            .map { groupDto ->
                groupDto.toGroup()
            }
    }

    override suspend fun getGroupsById(ids: List<Long>): Reaction<List<Group>> = Reaction.on {
        wordDao.getGroups(ids)
            .map { groupDto ->
                groupDto.toGroup()
            }
    }
}
