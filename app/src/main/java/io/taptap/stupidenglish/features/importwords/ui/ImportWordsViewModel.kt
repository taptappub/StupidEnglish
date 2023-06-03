package io.taptap.stupidenglish.features.importwords.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.mapper.toGroupsList
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.base.model.WordWithGroups
import io.taptap.stupidenglish.features.importwords.domain.ImportWordsInteractor
import io.taptap.uikit.group.NoGroup
import io.taptap.uikit.group.getTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import taptap.pub.doOnComplete
import taptap.pub.doOnError
import taptap.pub.doOnSuccess
import taptap.pub.fold
import taptap.pub.handle
import javax.inject.Inject

@HiltViewModel
class ImportWordsViewModel @Inject constructor(
    private val interactor: ImportWordsInteractor
) : BaseViewModel<ImportWordsContract.Event, ImportWordsContract.State, ImportWordsContract.Effect>() {

    private var words: List<Word> = mutableListOf()

    var link by mutableStateOf("")
        private set

    @JvmName("setLink1")
    fun setLink(newLink: String) {
        link = newLink
        viewModelScope.launch(Dispatchers.IO) {
            handleLinkChanging(newLink)
        }
    }

    var group by mutableStateOf("")
        private set

    @JvmName("setGroup1")
    fun setGroup(newGroup: String) {
        group = newGroup
    }

    init {
        if (!interactor.isImportTutorialShown) {
            setEffect { ImportWordsContract.Effect.Navigation.GoToImportTutorial }
            interactor.isImportTutorialShown = true
        }

        viewModelScope.launch(Dispatchers.IO) { getGroupsList() }
    }

    override fun setInitialState() = ImportWordsContract.State(
        selectedGroups = listOf(NoGroup),
        isAddGroup = false,
        groups = emptyList(),
        importWordState = ImportWordsContract.ImportWordState.None,
        parsingState = ImportWordsContract.ParsingState.None
    )

    override suspend fun handleEvents(event: ImportWordsContract.Event) {
        when (event) {
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
            is ImportWordsContract.Event.OnAddGroupClick -> {
                setState { copy(isAddGroup = true) }
                setEffect { ImportWordsContract.Effect.ShowBottomSheet }
            }
            is ImportWordsContract.Event.OnApplyGroup -> {
                saveGroup(group)
            }
            is ImportWordsContract.Event.OnGroupAddingCancel -> {
                group = ""
                setState { copy(isAddGroup = false) }
            }
            is ImportWordsContract.Event.OnTutorialClick -> {
                setEffect { ImportWordsContract.Effect.Navigation.GoToImportTutorial }
            }
            is ImportWordsContract.Event.OnBackClick ->
                setEffect { ImportWordsContract.Effect.Navigation.BackToWordList }
        }
    }

    var debounceJob: Job? = null

    private suspend fun handleLinkChanging(link: String) {
        when {
            link.isEmpty() -> {
                setImportWordState(ImportWordsContract.ImportWordState.None)
                debounceJob?.cancel()
            }
            interactor.check(link) -> {
                setImportWordState(ImportWordsContract.ImportWordState.InProgress)

                debounceJob?.cancel()
                debounceJob = CoroutineScope(currentCoroutineContext()).launch {
                    delay(700)
                    interactor.getWordsFromGoogleSheetTable(link)
                        .doOnSuccess { words = it }
                        .fold(
                            success = { ImportWordsContract.ImportWordState.HasLink },
                            error = { ImportWordsContract.ImportWordState.Error(R.string.impw_network_error) }
                        )
                        .let { state -> setImportWordState(state) }
                }
            }
            else -> {
                setImportWordState(ImportWordsContract.ImportWordState.Error(R.string.impw_incorrect_link_error))
            }
        }
    }


    private fun setImportWordState(state: ImportWordsContract.ImportWordState) {
        setState { copy(importWordState = state) }
    }

    private suspend fun importWords() {
        setState {
            copy(
                parsingState = ImportWordsContract.ParsingState.InProgress,
            )
        }

        val newWords = words.map {
            WordWithGroups(
                word = Word(
                    id = it.id,
                    word = it.word.trim(),
                    description = it.description.trim(),
                    points = it.points
                ),
                groups = viewState.value.selectedGroups.map {
                    Group(
                        id = it.id,
                        name = ""//don't use
                    )
                }
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

    private suspend fun saveGroup(group: String) {
        val trimGroup = group.trim()
        interactor.saveGroup(trimGroup)
            .doOnComplete {
                setGroup("")
                setState { copy(isAddGroup = false) }
                setEffect { ImportWordsContract.Effect.HideBottomSheet }
            }
    }

    private suspend fun getGroupsList() {
        interactor.observeGroupList()
            .handle(
                success = { groupList ->
                    groupList.collect { list ->
                        val groups = list.toGroupsList(withNoGroup = true)
                        setState {
                            copy(groups = groups)
                        }
                    }
                },
                error = {
                    setEffect { ImportWordsContract.Effect.GetGroupsError(R.string.impw_get_groups_error) }
                }
            )
    }
}
