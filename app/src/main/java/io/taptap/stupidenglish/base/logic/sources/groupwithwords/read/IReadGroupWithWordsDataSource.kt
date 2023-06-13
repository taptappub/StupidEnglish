package io.taptap.stupidenglish.base.logic.sources.groupwithwords.read

import io.taptap.stupidenglish.base.model.GroupWithWords
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.flow.Flow
import taptap.pub.Reaction

interface IReadGroupWithWordsDataSource {
    fun observeGroupWithWords(groupId: Long = NoGroup.id): Flow<GroupWithWords>
    suspend fun getGroupWithWords(groupId: Long = NoGroup.id): Reaction<GroupWithWords>
    suspend fun getWordsCountInGroup(groupId: Long = NoGroup.id): Reaction<Int>
}