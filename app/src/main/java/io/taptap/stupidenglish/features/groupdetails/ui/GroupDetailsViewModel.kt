package io.taptap.stupidenglish.features.groupdetails.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.mapper.toGroupItemUI
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.groupdetails.data.GroupDetailsRepository
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsButtonUI
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsDynamicTitleUI
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsEmptyUI
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsUIModel
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsWordItemUI
import io.taptap.uikit.group.GroupListItemsModel
import io.taptap.uikit.group.GroupListModel
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taptap.pub.doOnComplete
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: GroupDetailsRepository
) : BaseViewModel<GroupDetailsContract.Event, GroupDetailsContract.State, GroupDetailsContract.Effect>() {

    init {
        val currentGroupId = stateHandle.get<String>(NavigationKeys.Arg.GROUP_ID)?.toLong()
            ?: error("No group was passed to AddSentenceViewModel.")

        viewModelScope.launch(Dispatchers.IO) {
            getWordList(currentGroupId)
        }
    }

    override fun setInitialState() = GroupDetailsContract.State(
        group = NoGroup,
        wordList = listOf(),
        isLoading = true,
        deletedWordIds = mutableListOf(),
    )

    override suspend fun handleEvents(event: GroupDetailsContract.Event) {
        when (event) {
            is GroupDetailsContract.Event.OnDismiss -> {
                predeleteWord(event.item)
            }
            is GroupDetailsContract.Event.OnRemoveGroupClick -> {
                removeGroups(listOf(viewState.value.group))
            }
            is GroupDetailsContract.Event.OnApplyDismiss -> {
                deleteWords()
            }
            is GroupDetailsContract.Event.OnRecover -> {
                val words = repository.getGroupWithWords(viewState.value.group.id).takeOrReturn {
                    setEffect { GroupDetailsContract.Effect.GetWordsError(R.string.word_get_list_error) }
                    setState { copy(deletedWordIds = mutableListOf()) }
                    return
                }.words

                makeWordList(words.reversed(), viewState.value.group)
            }
            is GroupDetailsContract.Event.OnBackClick ->
                setEffect { GroupDetailsContract.Effect.Navigation.BackTo }
            is GroupDetailsContract.Event.OnRecovered -> {
                setState { copy(deletedWordIds = mutableListOf()) }
            }
            is GroupDetailsContract.Event.OnAddWordClick -> {
                setEffect {
                    GroupDetailsContract.Effect.Navigation.ToAddWordWithGroup(
                        group = viewState.value.group
                    )
                }
            }
            is GroupDetailsContract.Event.ToFlashCards -> {
                setEffect {
                    GroupDetailsContract.Effect.Navigation.ToFlashCards(
                        group = viewState.value.group
                    )
                }
            }
            is GroupDetailsContract.Event.ToAddSentence -> {
                setEffect {
                    GroupDetailsContract.Effect.Navigation.ToFlashCards(
                        group = viewState.value.group
                    )
                }
            }
            is GroupDetailsContract.Event.OnImportWordsClick -> {
                setEffect {
                    GroupDetailsContract.Effect.Navigation.ToImportWords(
                        group = viewState.value.group
                    )
                }
            }
        }
    }

    private fun predeleteWord(item: GroupDetailsWordItemUI) {
        val mutableDeletedWordIds = viewState.value.deletedWordIds.toMutableList()
        mutableDeletedWordIds.add(item.id)
        val list = viewState.value.wordList.toMutableList()
        list.remove(item)
        setState { copy(wordList = list, deletedWordIds = mutableDeletedWordIds) }
        setEffect { GroupDetailsContract.Effect.ShowRecover }
    }

    private suspend fun deleteWords() {
        repository.deleteWords(viewState.value.deletedWordIds)
        setState { copy(deletedWordIds = mutableListOf()) }
    }

    private suspend fun removeGroups(removedGroups: List<GroupListModel>) {
        repository.removeGroups(removedGroups.map { it.id })
            .doOnComplete {
                setEffect { GroupDetailsContract.Effect.Navigation.BackTo }
            }
    }

    private suspend fun getWordList(groupId: Long) {
        val groupWithWordsFlow = repository.observeGroupWithWords(groupId).takeOrReturn {
            setEffect { GroupDetailsContract.Effect.GetWordsError(R.string.grps_get_groups_error) }
            return
        }

        groupWithWordsFlow.collect {
            val group = it.group?.toGroupItemUI() ?: NoGroup
            setState { copy(group = group) }

            val reversedWordList = it.words.reversed()
            makeWordList(reversedWordList, group)
        }
    }

    private fun makeWordList(list: List<Word>, group: GroupListItemsModel) {
        val words: List<GroupDetailsWordItemUI> = list.toWordsList()
        val mainList = mutableListOf<GroupDetailsUIModel>()

        mainList.add(
            GroupDetailsButtonUI(
                valueRes = R.string.grdt_flashcards,
                buttonId = GroupDetailsContract.ButtonId.flashcards
            )
        )
        mainList.add(
            GroupDetailsButtonUI(
                valueRes = R.string.grdt_learn,
                buttonId = GroupDetailsContract.ButtonId.learn
            )
        )
        mainList.add(
            GroupDetailsButtonUI(
                valueRes = R.string.grdt_share,
                buttonId = GroupDetailsContract.ButtonId.share
            )
        )
        mainList.add(
            GroupDetailsButtonUI(
                valueRes = R.string.grdt_remove,
                buttonId = GroupDetailsContract.ButtonId.remove
            )
        )

        mainList.add(GroupDetailsDynamicTitleUI(currentGroup = group))
        if (words.isEmpty()) {
            mainList.add(GroupDetailsEmptyUI(descriptionRes = R.string.word_empty_list_description))
        } else {
            mainList.addAll(words)
        }

        setState {
            copy(wordList = mainList, isLoading = false)
        }
    }
}

private fun List<Word>.toWordsList(): List<GroupDetailsWordItemUI> {
    return this.map {
        GroupDetailsWordItemUI(
            id = it.id,
            word = it.word,
            description = it.description
        )
    }
}