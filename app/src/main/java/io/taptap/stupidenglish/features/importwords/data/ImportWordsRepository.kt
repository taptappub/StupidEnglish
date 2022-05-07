package io.taptap.stupidenglish.features.importwords.data

import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.logic.sources.groups.read.IReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.read.NoGroup
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.keys.IKeysDataSource
import io.taptap.stupidenglish.base.logic.sources.words.write.IWriteWordsDataSource
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.importwords.data.model.GoogleTableModel
import io.taptap.stupidenglish.features.importwords.data.network.IGoogleSheetApi
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportWordsRepository @Inject constructor(
    readGroupsDataSource: IReadGroupsDataSource,
    writeGroupsDataSource: IWriteGroupsDataSource,
    writeWordsDataSource: IWriteWordsDataSource,
    private val settings: Settings,
    private val keysDataSource: IKeysDataSource,
    private val googleSheetApi: IGoogleSheetApi
) : IReadGroupsDataSource by readGroupsDataSource,
    IWriteWordsDataSource by writeWordsDataSource,
    IWriteGroupsDataSource by writeGroupsDataSource {

    suspend fun getWordsFromGoogleSheetTable(spreadsheetId: String): Reaction<List<Word>> =
        Reaction.on {
            googleSheetApi
                .getTableData(
                    spreadsheetId = spreadsheetId,
                    apiKey = keysDataSource.googleApiKey
                )
                .toWordList()
        }

    var isImportTutorialShown: Boolean
        get() {
            return settings.isImportTutorialShown
        }
        set(value) {
            settings.isImportTutorialShown = value
        }
}

private fun GoogleTableModel.toWordList(): List<Word> = this.values.map {
    Word(
        id = -1,
        word = it[2],
        description = it[3],
        groupsIds = listOf(NoGroup.id),
        points = 0
    )
}
