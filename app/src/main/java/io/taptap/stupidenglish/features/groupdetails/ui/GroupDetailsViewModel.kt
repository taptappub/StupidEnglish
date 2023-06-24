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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import taptap.pub.doOnComplete
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val repository: GroupDetailsRepository
) : BaseViewModel<GroupDetailsContract.Event, GroupDetailsContract.State, GroupDetailsContract.Effect>() {

    private val currentGroupId = stateHandle.get<String>(NavigationKeys.Arg.GROUP_ID)?.toLong()
        ?: error("No group was passed to AddSentenceViewModel.")
    private lateinit var currentGroup: GroupListItemsModel

    val mainList: StateFlow<List<GroupDetailsUIModel>> =
        repository.observeGroupWithWords(currentGroupId)
            .map {
                currentGroup = it.group?.toGroupItemUI() ?: NoGroup
                val reversedWordList = it.words.reversed().toWordsList()
                makeMainList(reversedWordList, currentGroup)
            }.onStart {
                setState { copy(isLoading = false) }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )


    override fun setInitialState() = GroupDetailsContract.State(
        isLoading = true,
        deletedWords = mutableListOf(),
    )

    override suspend fun handleEvents(event: GroupDetailsContract.Event) {
        when (event) {
            is GroupDetailsContract.Event.OnDismiss -> {
                deleteWord(event.item)
            }
            is GroupDetailsContract.Event.OnRemoveGroupClick -> {
                removeGroups(listOf(currentGroup))
            }
            is GroupDetailsContract.Event.OnApplyDismiss -> {
                setState { copy(deletedWords = mutableListOf()) }
            }
            is GroupDetailsContract.Event.OnRecover -> {
                val mutableDeletedWords = viewState.value.deletedWords
                repository.saveWords(mutableDeletedWords)
            }
            is GroupDetailsContract.Event.OnBackClick ->
                setEffect { GroupDetailsContract.Effect.Navigation.BackTo }
            is GroupDetailsContract.Event.OnRecovered -> {
                setState { copy(deletedWords = mutableListOf()) }
            }
            is GroupDetailsContract.Event.OnAddWord -> {
                setEffect {
                    GroupDetailsContract.Effect.Navigation.ToAddWordWithGroup(currentGroup)
                }
            }
            is GroupDetailsContract.Event.OnImportWordsClick -> {
                setEffect {
                    GroupDetailsContract.Effect.Navigation.ToImportWords(currentGroup)
                }
            }
            is GroupDetailsContract.Event.ToFlashCards -> {
                setEffect {
                    GroupDetailsContract.Effect.Navigation.ToFlashCards(
                        group = currentGroup
                    )
                }
            }
            is GroupDetailsContract.Event.ToAddSentence -> {
                setEffect {
                    GroupDetailsContract.Effect.Navigation.ToAddSentence(
                        group = currentGroup
                    )
                }
            }
        }
    }

    private suspend fun deleteWord(item: GroupDetailsWordItemUI) {
        val mutableDeletedWords = viewState.value.deletedWords.toMutableList()
        val wordWithGroups = repository.getWordWithGroups(wordId = item.id).takeOrReturn {
            setEffect { GroupDetailsContract.Effect.GetWordsError(R.string.word_get_list_error) }
            return
        }
        mutableDeletedWords.add(wordWithGroups)
        repository.deleteWords(listOf(item.id))
        setState { copy(deletedWords = mutableDeletedWords) }
        setEffect { GroupDetailsContract.Effect.ShowRecover }
    }

    private suspend fun removeGroups(removedGroups: List<GroupListModel>) {
        repository.removeGroups(removedGroups.map { it.id })
            .doOnComplete {
                setEffect { GroupDetailsContract.Effect.Navigation.BackTo }
            }
    }

    private fun makeMainList(
        words: List<GroupDetailsWordItemUI>,
        group: GroupListItemsModel
    ): List<GroupDetailsUIModel> {
        val mainList = mutableListOf<GroupDetailsUIModel>()

        mainList.add(GroupDetailsDynamicTitleUI(currentGroup = group))

        mainList.add(
            GroupDetailsButtonUI(
                valueRes = R.string.grdt_import_words,
                buttonId = GroupDetailsContract.ButtonId.import
            )
        )
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

        if (words.isEmpty()) {
            mainList.add(GroupDetailsEmptyUI(descriptionRes = R.string.word_empty_list_description))
        } else {
            mainList.addAll(words)
        }

        return mainList
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