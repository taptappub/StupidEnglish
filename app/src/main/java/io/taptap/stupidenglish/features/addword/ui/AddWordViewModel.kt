package io.taptap.stupidenglish.features.addword.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.mapper.toGroupsList
import io.taptap.stupidenglish.features.addword.data.AddWordRepository
import io.taptap.uikit.group.GroupListModel
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.doOnComplete
import taptap.pub.doOnSuccess
import taptap.pub.handle
import taptap.pub.map
import javax.inject.Inject

@HiltViewModel
class AddWordViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: AddWordRepository
) : BaseViewModel<AddWordContract.Event, AddWordContract.State, AddWordContract.Effect>() {

    var word by mutableStateOf("")
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) { getGroupsList() }

        val currentGroupId = stateHandle.get<String>(NavigationKeys.Arg.GROUP_ID)?.toLong()
        if (currentGroupId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.getGroupsById(listOf(currentGroupId))
                    .map { it.toGroupsList(withNoGroup = true) }
                    .doOnSuccess {
                        setState { copy(selectedGroups = it) }
                    }
            }
        }

        val word = stateHandle.get<String>(NavigationKeys.Arg.WORD)
        if (word != null) {
            setWord(word)
            setState { copy(addWordState = AddWordContract.AddWordState.HasWord) }
        }
    }

    @JvmName("setWord1")
    fun setWord(newWord: String) {
        word = newWord
    }

    var description by mutableStateOf("")
        private set

    @JvmName("setSentence1")
    fun setDescription(newDescription: String) {
        description = newDescription
    }

    var group by mutableStateOf("")
        private set

    @JvmName("setGroup1")
    fun setGroup(newGroup: String) {
        group = newGroup
    }

    override fun setInitialState() = AddWordContract.State(
        selectedGroups = listOf(NoGroup),
        groups = emptyList(),
        isAddGroup = false,
        addWordState = AddWordContract.AddWordState.None
    )

    override suspend fun handleEvents(event: AddWordContract.Event) {
        when (event) {
            is AddWordContract.Event.OnBackClick -> {
                if (word.isNotEmpty()
                    && description.isNotEmpty()
                    && viewState.value.addWordState == AddWordContract.AddWordState.HasDescription
                ) {
                    saveWord(onSuccess = {
                        setEffect { AddWordContract.Effect.Navigation.BackToWordList }
                    })
                } else {
                    setEffect { AddWordContract.Effect.Navigation.BackToWordList }
                }
            }
            is AddWordContract.Event.OnNewWord -> {
                saveWord(onSuccess = {})
            }
            is AddWordContract.Event.OnWord ->
                setState { copy(addWordState = AddWordContract.AddWordState.HasWord) }
            is AddWordContract.Event.OnDescription ->
                setState { copy(addWordState = AddWordContract.AddWordState.HasDescription) }
            is AddWordContract.Event.BackToNoneState -> {
                setState {
                    copy(
                        addWordState = AddWordContract.AddWordState.None,
                    )
                }
                setDescription("")
            }
            is AddWordContract.Event.BackToWordState -> {
                setState {
                    copy(
                        addWordState = AddWordContract.AddWordState.HasWord,
                        selectedGroups = listOf(NoGroup)
                    )
                }
            }
            is AddWordContract.Event.OnWaitingDescriptionError -> setEffect {
                AddWordContract.Effect.WaitingForDescriptionError(
                    R.string.addw_description_not_found_error
                )
            }

            is AddWordContract.Event.OnGroupSelect -> {
                val selectedGroups = ArrayList(viewState.value.selectedGroups)
                if (selectedGroups.contains(event.item)) {
                    selectedGroups.remove(event.item)
                } else {
                    selectedGroups.add(event.item)
                }
                setState { copy(selectedGroups = selectedGroups) }
            }
            is AddWordContract.Event.OnAddGroupClick -> {
                setState { copy(isAddGroup = true) }
                setEffect { AddWordContract.Effect.ShowBottomSheet }
            }
            is AddWordContract.Event.OnApplyGroup -> {
                saveGroup(group)
            }
            is AddWordContract.Event.OnGroupAddingCancel -> {
                setState { copy(isAddGroup = false) }
            }
        }
    }

    private suspend fun saveWord(onSuccess: () -> Unit) {
        val selectedGroups = viewState.value.selectedGroups
        saveWord(word, description, selectedGroups, onSuccess)

        setState {
            copy(
                addWordState = AddWordContract.AddWordState.None
            )
        }
        setWord("")
        setDescription("")
        setGroup("")
    }

    private suspend fun saveWord(
        word: String,
        description: String,
        groups: List<GroupListModel>,
        onSuccess: () -> Unit
    ) {
        val trimWord = word.trim()
        val trimDescription = description.trim()
        val groupsIds = groups.mapNotNull {
            if (it.id == NoGroup.id) null else it.id
        }
        repository.saveWord(trimWord, trimDescription, groupsIds)
            .handle(
                success = {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                },
                error = {
                    withContext(Dispatchers.Main) {
                        setEffect { AddWordContract.Effect.SaveError(R.string.addw_save_error) }
                    }
                }
            )
    }

    private suspend fun saveGroup(group: String) {
        val trimGroup = group.trim()
        repository.saveGroup(trimGroup)
            .doOnComplete {
                setGroup("")
                setState { copy(isAddGroup = false) }
                setEffect { AddWordContract.Effect.HideBottomSheet }
            }
    }

    private suspend fun getGroupsList() {
        repository.observeGroupList()
            .onEach { list ->
                val groups = list.toGroupsList(withNoGroup = true)
                setState {
                    copy(groups = groups)
                }
            }
            .launchIn(viewModelScope)
    }
}
