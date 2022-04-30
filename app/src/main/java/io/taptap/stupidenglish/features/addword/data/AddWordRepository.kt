package io.taptap.stupidenglish.features.addword.data

import io.taptap.stupidenglish.base.logic.sources.groups.IGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.IWordsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWordRepository @Inject constructor(
    wordsDataSource: IWordsDataSource,
    groupsDataSource: IGroupsDataSource
) : IGroupsDataSource by groupsDataSource,
    IWordsDataSource by wordsDataSource
