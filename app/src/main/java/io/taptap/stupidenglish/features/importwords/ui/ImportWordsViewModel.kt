package io.taptap.stupidenglish.features.importwords.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.sources.groups.GroupItemUI
import io.taptap.stupidenglish.base.logic.sources.groups.GroupListModels
import io.taptap.stupidenglish.base.logic.sources.groups.NoGroup
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.importwords.data.ImportWordsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import taptap.pub.doOnError
import taptap.pub.doOnSuccess
import taptap.pub.takeOrReturn
import javax.inject.Inject

private val linkRegExp = "^https://sheets.googleapis.com/v([45])/spreadsheets/.+".toRegex()

@HiltViewModel
class ImportWordsViewModel @Inject constructor(
    private val repository: ImportWordsRepository
) : BaseViewModel<ImportWordsContract.Event, ImportWordsContract.State, ImportWordsContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) { getGroupsList() }
    }

    override fun setInitialState() = ImportWordsContract.State(
        link = "",
        isWrongLink = false,
        selectedGroups = listOf(NoGroup),
        groups = emptyList(),
        importWordState = ImportWordsContract.ImportWordState.None,
        parsingState = ImportWordsContract.ParsingState.None
    )

    override fun handleEvents(event: ImportWordsContract.Event) {
        when (event) {
            is ImportWordsContract.Event.OnLinkChanging -> {
                setState { copy(link = event.value) }

                if (event.value matches linkRegExp) {
                    setState {
                        copy(
                            isWrongLink = false,
                            importWordState = ImportWordsContract.ImportWordState.HasLink
                        )
                    }
                } else {
                    setState {
                        copy(
                            isWrongLink = true,
                            importWordState = ImportWordsContract.ImportWordState.None
                        )
                    }
                }
            }
            is ImportWordsContract.Event.OnGroupSelect -> {
                val selectedGroups = ArrayList(viewState.value.selectedGroups)
                if (selectedGroups.contains(event.item)) {
                    selectedGroups.remove(event.item)
                } else {
                    selectedGroups.add(event.item)
                }
                setState { copy(selectedGroups = selectedGroups) }
            }
            is ImportWordsContract.Event.OnImportClick ->
                viewModelScope.launch(Dispatchers.IO) {
                    importWords()
                }
        }
    }

    private suspend fun importWords() {
        setState {
            copy(
                parsingState = ImportWordsContract.ParsingState.InProgress,
            )
        }
        val words = repository.getWordsFromGoogleSheetTable(viewState.value.link)
            .takeOrReturn {
                setTemporaryState(
                    tempReducer = {
                        copy(
                            parsingState = ImportWordsContract.ParsingState.Failed,
                        )
                    },
                    duration = 2000
                )
                return
            }

        val groupsIds = viewState.value.selectedGroups.map {
            it.id
        }

        val newWords = words.map {
            Word(
                id = it.id,
                word = it.word,
                description = it.description,
                points = it.points,
                groupsIds = groupsIds
            )
        }

        repository.saveWords(newWords)
            .doOnSuccess {
                setState {
                    copy(
                        parsingState = ImportWordsContract.ParsingState.Success,
                    )
                }
                delay(2000)
                setEffect { ImportWordsContract.Effect.Navigation.BackToWordList }
            }
            .doOnError {
                setTemporaryState(
                    tempReducer = {
                        copy(
                            parsingState = ImportWordsContract.ParsingState.Failed,
                        )
                    },
                    duration = 2000
                )
            }
    }

    private suspend fun getGroupsList() {
        val groupList = repository.observeGroupList().takeOrReturn {
            setEffect { ImportWordsContract.Effect.GetGroupsError(R.string.impw_get_groups_error) }
            return
        }
        groupList.collect { list ->
            val groups = makeGroupsList(list)
            setState {
                copy(groups = groups)
            }
        }
    }

    private fun makeGroupsList(groupsList: List<Group>): List<GroupListModels> {
        val groupList = mutableListOf<GroupListModels>()
        groupList.add(NoGroup)

        groupList.addAll(groupsList.map {
            GroupItemUI(
                id = it.id,
                name = it.name
            )
        })

        return groupList
    }
}
