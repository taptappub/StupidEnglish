package io.taptap.stupidenglish.features.addword.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.isNotEmpty
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListModels
import io.taptap.uikit.group.NoGroup
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.features.addword.data.AddWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.handle
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class AddWordViewModel @Inject constructor(
    private val repository: AddWordRepository
) : BaseViewModel<AddWordContract.Event, AddWordContract.State, AddWordContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) { getGroupsList() }
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

    override fun setInitialState() = AddWordContract.State(
        selectedGroups = listOf(NoGroup),
        dialogSelectedGroups = listOf(NoGroup),
        groups = emptyList(),
        addWordState = AddWordContract.AddWordState.None
    )

    override suspend fun handleEvents(event: AddWordContract.Event) {
        when (event) {
            is AddWordContract.Event.OnBackClick ->
                setEffect { AddWordContract.Effect.Navigation.BackToWordList }
            is AddWordContract.Event.OnWord ->
                setState { copy(addWordState = AddWordContract.AddWordState.HasWord) }
            is AddWordContract.Event.BackToNoneState -> {
                setState {
                    copy(
                        addWordState = AddWordContract.AddWordState.None,
                    )
                }
                setDescription("")
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
                val selectedGroups = ArrayList(viewState.value.dialogSelectedGroups)
                if (selectedGroups.contains(event.item)) {
                    selectedGroups.remove(event.item)
                } else {
                    selectedGroups.add(event.item)
                }
                setState { copy(dialogSelectedGroups = selectedGroups) }
            }
            is AddWordContract.Event.OnChooseGroupBottomSheetCancel -> {
                setState { copy(dialogSelectedGroups = listOf(NoGroup)) }
                setEffect { AddWordContract.Effect.HideChooseGroupBottomSheet }
            }

            is AddWordContract.Event.OnGroupsClick -> {
                val selectedGroups = ArrayList(viewState.value.selectedGroups)
                setState { copy(dialogSelectedGroups = selectedGroups) }
                setEffect { AddWordContract.Effect.ShowChooseGroupBottomSheet }
            }
            is AddWordContract.Event.OnGroupsChosenConfirmClick -> {
                val dialogSelectedGroups = ArrayList(viewState.value.dialogSelectedGroups)
                setState {
                    copy(
                        selectedGroups = dialogSelectedGroups
                    )
                }
                setEffect { AddWordContract.Effect.HideChooseGroupBottomSheet }
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

    private suspend fun getGroupsList() {
        val groupList = repository.observeGroupList().takeOrReturn {
            setEffect { AddWordContract.Effect.GetWordsError(R.string.addw_get_groups_error) }
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
