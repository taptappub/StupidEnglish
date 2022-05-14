package io.taptap.stupidenglish.features.importwords.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.sources.groups.read.GroupItemUI
import io.taptap.stupidenglish.base.logic.sources.groups.read.GroupListModels
import io.taptap.stupidenglish.base.logic.sources.groups.read.NoGroup
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.importwords.data.ImportWordsRepository
import io.taptap.stupidenglish.features.importwords.domain.ImportWordsInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import taptap.pub.Reaction
import taptap.pub.doOnComplete
import taptap.pub.doOnError
import taptap.pub.doOnSuccess
import taptap.pub.fold
import taptap.pub.takeOrReturn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class ImportWordsViewModel @Inject constructor(
    private val interactor: ImportWordsInteractor
) : BaseViewModel<ImportWordsContract.Event, ImportWordsContract.State, ImportWordsContract.Effect>() {

    private var words: List<Word> = mutableListOf()

    init {
        if (!interactor.isImportTutorialShown) {
            setEffect { ImportWordsContract.Effect.Navigation.GoToImportTutorial }
            interactor.isImportTutorialShown = true
        }
        viewModelScope.launch(Dispatchers.IO) { getGroupsList() }
    }

    override fun setInitialState() = ImportWordsContract.State(
        link = "",
        selectedGroups = listOf(NoGroup),
        group = "",
        isAddGroup = false,
        groups = emptyList(),
        importWordState = ImportWordsContract.ImportWordState.None,
        parsingState = ImportWordsContract.ParsingState.None
    )

    override suspend fun handleEvents(event: ImportWordsContract.Event) {
        when (event) {
            is ImportWordsContract.Event.OnLinkChanging -> {
                val link = event.value
                setState { copy(link = link) }
                handleLinkChanging(link)
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
            is ImportWordsContract.Event.OnAddGroupClick -> {
                setState { copy(isAddGroup = true) }
                setEffect { ImportWordsContract.Effect.ShowBottomSheet }
            }
            is ImportWordsContract.Event.OnApplyGroup -> {
                saveGroup(viewState.value.group)
            }
            is ImportWordsContract.Event.OnGroupAddingCancel -> {
                setState { copy(group = "", isAddGroup = false) }
            }
            is ImportWordsContract.Event.OnGroupChanging -> {
                setState { copy(group = event.value) }
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
                    delay(500)
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

    private fun saveGroup(group: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.saveGroup(group)
                .doOnComplete {
                    setState { copy(group = "", isAddGroup = false) }
                    setEffect { ImportWordsContract.Effect.HideBottomSheet }
                }
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

suspend fun <T> CoroutineScope.debounce(
    waitMs: Long = 300L,
    destinationFunction: (T) -> Unit
): suspend (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = coroutineScope {
            launch {
                delay(waitMs)
                destinationFunction(param)
            }
        }
    }
}
