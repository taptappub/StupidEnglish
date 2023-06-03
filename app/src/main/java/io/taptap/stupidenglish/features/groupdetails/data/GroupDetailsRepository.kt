package io.taptap.stupidenglish.features.groupdetails.data

import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groupwithwords.read.IReadGroupWithWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.write.IWriteWordsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupDetailsRepository @Inject constructor(
    randomWordsDataSource: IRandomWordsDataSource,
    readGroupWithWordsDataSource: IReadGroupWithWordsDataSource,
    writeWordsDataSource: IWriteWordsDataSource,
    writeGroupsDataSource: IWriteGroupsDataSource,
) : IRandomWordsDataSource by randomWordsDataSource,
    IReadGroupWithWordsDataSource by readGroupWithWordsDataSource,
    IWriteWordsDataSource by writeWordsDataSource,
    IWriteGroupsDataSource by writeGroupsDataSource