package io.taptap.stupidenglish.features.importwords.domain

import android.util.Log
import io.taptap.stupidenglish.base.logic.sources.groups.read.IReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.write.IWriteWordsDataSource
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.importwords.data.ImportWordsRepository
import taptap.pub.Reaction
import taptap.pub.flatMap
import javax.inject.Inject

private val linkRegExp = "https://docs\\.google\\.com/spreadsheets/d/(.*?)/(.)+".toRegex()

class ImportWordsInteractor @Inject constructor(
    private val repository: ImportWordsRepository
) : IWriteWordsDataSource by repository,
    IReadGroupsDataSource by repository,
    IWriteGroupsDataSource by repository {

    var isImportTutorialShown: Boolean
        get() {
            return repository.isImportTutorialShown
        }
        set(value) {
            repository.isImportTutorialShown = value
        }

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
        Log.d("ImportWordsInteractor", "getSpreadSheetId link $link")
        Log.d("ImportWordsInteractor", "getSpreadSheetId linkRegExp ${linkRegExp.pattern}")
        return Reaction.on { linkRegExp.find(link)?.groupValues?.get(1) ?: error("getSpreadSheetId null")}
    }
}