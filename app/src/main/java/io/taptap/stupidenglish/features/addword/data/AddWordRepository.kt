package io.taptap.stupidenglish.features.addword.data

import io.taptap.stupidenglish.base.logic.sources.groups.read.IReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.write.IWriteWordsDataSource
import io.taptap.stupidenglish.base.model.Group
import kotlinx.coroutines.flow.Flow
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWordRepository @Inject constructor(
    readGroupsDataSource: IReadGroupsDataSource,
    writeGroupsDataSource: IWriteGroupsDataSource,
    writeWordsDataSource: IWriteWordsDataSource
) : IReadGroupsDataSource by readGroupsDataSource,
    IWriteWordsDataSource by writeWordsDataSource,
    IWriteGroupsDataSource by writeGroupsDataSource