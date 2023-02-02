package io.taptap.stupidenglish.base.logic.sources.groups.write

import taptap.pub.Reaction

interface IWriteGroupsDataSource {
    suspend fun saveGroup(group: String): Reaction<Long>
    suspend fun removeGroups(groupsIds: List<Long>): Reaction<Unit>
}
