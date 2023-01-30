package io.taptap.stupidenglish.features.addword.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.ui.helpers.GroupViewModelHelper
import io.taptap.stupidenglish.base.ui.helpers.IGroupViewModelHelper
import io.taptap.stupidenglish.features.addword.data.AddWordRepository
import io.taptap.uikit.group.GroupListModels
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.handle
import javax.inject.Inject

@HiltViewModel
class AddWordViewModel @Inject constructor(
    private val repository: AddWordRepository
) : BaseViewModel<AddWordContract.Event, AddWordContract.State, AddWordContract.Effect>(),
    IGroupViewModelHelper by GroupViewModelHelper(repository, repository) {

    init {
        setCoroutineScope(viewModelScope)
        getGroupsList()
    }

    var word by mutableStateOf("")
        private set

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
            is AddWordContract.Event.OnBackClick ->
                setEffect { AddWordContract.Effect.Navigation.BackToWordList }
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
            is AddWordContract.Event.OnSaveWord -> {
                setInitialState()
                val selectedGroups = viewState.value.selectedGroups
                saveWord(word, description, selectedGroups)
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

    private fun saveWord(
        word: String,
        description: String,
        groups: List<GroupListModels>
    ) {
        val trimWord = word.trim()
        val trimDescription = description.trim()
        viewModelScope.launch(Dispatchers.IO) {
            val groupsIds = groups.mapNotNull {
                if (it.id == NoGroup.id) null else it.id
            }
            repository.saveWord(trimWord, trimDescription, groupsIds)
                .handle(
                    success = {
                        withContext(Dispatchers.Main) {
                            setEffect { AddWordContract.Effect.Navigation.BackToWordList }
                        }
                    },
                    error = {
                        withContext(Dispatchers.Main) {
                            setEffect { AddWordContract.Effect.SaveError(R.string.addw_save_error) }
                        }
                    }
                )
        }
    }

    private fun saveGroup(group: String) {
        saveGroup(group, doOnComplete = {
            setGroup("")
            setState { copy(isAddGroup = false) }
            setEffect { AddWordContract.Effect.HideBottomSheet }
        })
    }

    private fun getGroupsList() {
        getGroupsList(
            collect = { groups ->
                setState {
                    copy(groups = groups)
                }
            },
            onError = {
                setEffect { AddWordContract.Effect.GetGroupsError(R.string.addw_get_groups_error) }
            }
        )
    }

}