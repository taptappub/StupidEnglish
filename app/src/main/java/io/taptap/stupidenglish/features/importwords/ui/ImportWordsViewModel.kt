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
import io.taptap.stupidenglish.features.importwords.domain.ImportWordsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import taptap.pub.Reaction
import taptap.pub.doOnError
import taptap.pub.doOnSuccess
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class ImportWordsViewModel @Inject constructor(
    private val interactor: ImportWordsInteractor
) : BaseViewModel<ImportWordsContract.Event, ImportWordsContract.State, ImportWordsContract.Effect>() {

    private var words: List<Word> = mutableListOf()

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

    override suspend fun handleEvents(event: ImportWordsContract.Event) {
        when (event) {
            is ImportWordsContract.Event.OnLinkChanging -> {
                setState { copy(link = event.value) }
                if (interactor.check(event.value)) {
                    flow<Reaction<List<Word>>> {
                        interactor.getWordsFromGoogleSheetTable(viewState.value.link)
                    }.collectLatest {
                        setState {
                            copy(
                                isWrongLink = it.isError(),
                                importWordState = if (it.isError()) {
                                    ImportWordsContract.ImportWordState.None
                                } else {
                                    ImportWordsContract.ImportWordState.HasLink
                                }
                            )
                        }
                        it.doOnSuccess { googleSheetList ->
                            words = googleSheetList
                        }
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

        interactor.saveWords(newWords)
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
        val groupList = interactor.observeGroupList().takeOrReturn {
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

fun <T> Reaction<T>.isError(): Boolean = this is Reaction.Error