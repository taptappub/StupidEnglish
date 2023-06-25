package io.taptap.stupidenglish.base.logic.sources.groups.read

import io.taptap.stupidenglish.base.model.Group
import kotlinx.coroutines.flow.Flow
import taptap.pub.Reaction

interface IReadGroupsDataSource {
    fun observeGroupList(): Flow<List<Group>>
    suspend fun getGroupList(): Reaction<List<Group>>
    suspend fun getGroupsById(ids: List<Long>): Reaction<List<Group>>
}
