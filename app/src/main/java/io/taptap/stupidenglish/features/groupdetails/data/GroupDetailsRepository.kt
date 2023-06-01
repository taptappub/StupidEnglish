package io.taptap.stupidenglish.features.groupdetails.data

import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.read.IReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.read.IReadWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.write.IWriteWordsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupDetailsRepository @Inject constructor(
    randomWordsDataSource: IRandomWordsDataSource,
    readWordsDataSource: IReadWordsDataSource,
    writeWordsDataSource: IWriteWordsDataSource,
    readGroupsDataSource: IReadGroupsDataSource,
    writeGroupsDataSource: IWriteGroupsDataSource,
) : IRandomWordsDataSource by randomWordsDataSource,
    IReadWordsDataSource by readWordsDataSource,
    IWriteWordsDataSource by writeWordsDataSource,
    IReadGroupsDataSource by readGroupsDataSource,
    IWriteGroupsDataSource by writeGroupsDataSource