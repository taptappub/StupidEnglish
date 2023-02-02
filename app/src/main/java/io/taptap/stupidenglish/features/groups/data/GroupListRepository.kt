package io.taptap.stupidenglish.features.groups.data

import io.taptap.stupidenglish.base.logic.sources.groups.read.IReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.read.IReadWordsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupListRepository @Inject constructor(
    readGroupsDataSource: IReadGroupsDataSource,
    readWordsDataSource: IReadWordsDataSource,
    writeGroupsDataSource: IWriteGroupsDataSource
) : IReadGroupsDataSource by readGroupsDataSource,
    IReadWordsDataSource by readWordsDataSource,
    IWriteGroupsDataSource by writeGroupsDataSource