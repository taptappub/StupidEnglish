package io.taptap.stupidenglish.features.importwords.domain

import io.taptap.stupidenglish.base.logic.sources.groups.IGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.IWordsDataSource
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.importwords.data.ImportWordsRepository
import taptap.pub.Reaction
import taptap.pub.flatMap
import javax.inject.Inject

private val linkRegExp = "https://docs\\.google\\.com/spreadsheets/d/(.*?)/(.)+".toRegex()

class ImportWordsInteractor @Inject constructor(
    private val repository: ImportWordsRepository
) : IWordsDataSource by repository,
    IGroupsDataSource by repository {
    fun check(value: String): Boolean {
        return value matches linkRegExp
    }

    suspend fun getWordsFromGoogleSheetTable(link: String): Reaction<List<Word>> {
        return getSpreadSheetId(link)
            .flatMap { spreadsheetid ->
                repository.getWordsFromGoogleSheetTable(spreadsheetid)
            }
    }

    private fun getSpreadSheetId(link: String): Reaction<String> {
        return Reaction.on { linkRegExp.find(link)!!.groupValues[1] }
    }
}