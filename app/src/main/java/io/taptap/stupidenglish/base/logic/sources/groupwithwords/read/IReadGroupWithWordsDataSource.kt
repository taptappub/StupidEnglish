package io.taptap.stupidenglish.base.logic.sources.groupwithwords.read

import io.taptap.stupidenglish.base.model.GroupWithWords
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.flow.Flow
import taptap.pub.Reaction

interface IReadGroupWithWordsDataSource {
    suspend fun observeGroupWithWords(groupId: Long = NoGroup.id): Reaction<Flow<GroupWithWords>>
    suspend fun getGroupWithWords(groupId: Long = NoGroup.id): Reaction<GroupWithWords>
    suspend fun getWordsCountInGroup(groupId: Long = NoGroup.id): Reaction<Int>
}