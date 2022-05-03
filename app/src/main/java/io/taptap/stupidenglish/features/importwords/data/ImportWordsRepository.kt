package io.taptap.stupidenglish.features.importwords.data

import io.taptap.stupidenglish.base.logic.sources.groups.IGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.NoGroup
import io.taptap.stupidenglish.base.logic.sources.keys.IKeysDataSource
import io.taptap.stupidenglish.base.logic.sources.words.IWordsDataSource
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.importwords.data.model.GoogleTableModel
import io.taptap.stupidenglish.features.importwords.data.network.IGoogleSheetApi
import taptap.pub.Reaction
import taptap.pub.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportWordsRepository @Inject constructor(
    groupsDataSource: IGroupsDataSource,
    wordsDataSource: IWordsDataSource,
    private val keysDataSource: IKeysDataSource,
    private val googleSheetApi: IGoogleSheetApi
) : IGroupsDataSource by groupsDataSource,
    IWordsDataSource by wordsDataSource {

    suspend fun getWordsFromGoogleSheetTable(spreadsheetId: String): Reaction<List<Word>> =
        Reaction.on {
            googleSheetApi
                .getTableData(
                    spreadsheetId = spreadsheetId,
                    apiKey = keysDataSource.googleApiKey
                )
                .toWordList()
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
