package io.taptap.stupidenglish.base.logic.groups

import io.taptap.stupidenglish.base.model.Group
import kotlinx.coroutines.flow.Flow
import taptap.pub.Reaction

interface IGroupsDataSource {
    suspend fun observeGroupList(): Reaction<Flow<List<Group>>>
    suspend fun getGroupList(): Reaction<List<Group>>
}