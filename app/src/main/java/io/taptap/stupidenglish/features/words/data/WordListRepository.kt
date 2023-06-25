package io.taptap.stupidenglish.features.words.data

import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.logic.sources.groups.read.IReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groupwithwords.read.IReadGroupWithWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.user.read.IReadUserDataSource
import io.taptap.stupidenglish.base.logic.sources.words.read.IReadWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.read.ReadWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.write.IWriteWordsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordListRepository @Inject constructor(
    readGroupsDataSource: IReadGroupsDataSource,
    writeGroupsDataSource: IWriteGroupsDataSource,
    readUserDataSource: IReadUserDataSource,
    readWordsDataSource: IReadWordsDataSource,
    readGroupWithWordsDataSource: IReadGroupWithWordsDataSource,
    writeWordsDataSource: IWriteWordsDataSource,
    private val settings: Settings
) : IReadGroupsDataSource by readGroupsDataSource,
    IWriteGroupsDataSource by writeGroupsDataSource,
    IReadUserDataSource by readUserDataSource,
    IReadWordsDataSource by readWordsDataSource,
    IReadGroupWithWordsDataSource by readGroupWithWordsDataSource,
    IWriteWordsDataSource by writeWordsDataSource {

    var isSentenceMotivationShown: Boolean
        get() {
            return settings.isSentenceMotivationShown
        }
        set(value) {
            settings.isSentenceMotivationShown = value
        }
}
