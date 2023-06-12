package io.taptap.stupidenglish.features.words.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.mapper.toGroupsList
import io.taptap.stupidenglish.base.logic.mapper.toWordsList
import io.taptap.stupidenglish.features.words.data.WordListRepository
import io.taptap.stupidenglish.features.words.ui.model.OnboardingWordUI
import io.taptap.stupidenglish.features.words.ui.model.WordListDynamicTitleUI
import io.taptap.stupidenglish.features.words.ui.model.WordListEmptyUI
import io.taptap.stupidenglish.features.words.ui.model.WordListGroupUI
import io.taptap.stupidenglish.features.words.ui.model.WordListItemUI
import io.taptap.stupidenglish.features.words.ui.model.WordListListModels
import io.taptap.stupidenglish.ui.MenuItem
import io.taptap.uikit.group.GroupListItemsModel
import io.taptap.uikit.group.GroupListModel
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import taptap.pub.doOnComplete
import taptap.pub.handle
import taptap.pub.takeOrReturn
import javax.inject.Inject

private const val WORDS_FOR_MOTIVATION = 1

@HiltViewModel
class WordListViewModel @Inject constructor(
    private val repository: WordListRepository
) : BaseViewModel<WordListContract.Event, WordListContract.State, WordListContract.Effect>() {

    val currentGroupFlow = MutableStateFlow<GroupListItemsModel>(NoGroup)

    val wordList: StateFlow<List<WordListListModels>> = currentGroupFlow
        .flatMapMerge { currentGroup ->
            repository.observeWordList(currentGroup.id)
                .map {
                    currentGroup to it
                }
        }.combine(repository.observeGroupList()) { pair, groups ->
            val wordList = pair.second.reversed().toWordsList()
            val currentGroup = pair.first
            val groupsList = groups.reversed().toGroupsList(withNoGroup = true)
            makeMainList(wordList, groupsList, currentGroup)
        }.onStart {
            setState { copy(isLoading = false) }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    var word by mutableStateOf("")
        private set

    var groupName by mutableStateOf("")
        private set

    fun setNewGroupName(newGroupName: String) {
        groupName = newGroupName
    }

    init {
        viewModelScope.launch(Dispatchers.IO) { showMotivation() }
        viewModelScope.launch(Dispatchers.IO) { getSavedUser() }
    }

    override fun setInitialState() = WordListContract.State(
        isLoading = true,
        groupMenuList = getGroupMenu(WordListContract.MenuType.Enabled),
        longClickedGroup = NoGroup,
        sheetContentType = WordListContract.SheetContentType.Motivation,
        deletedWords = mutableListOf(),
        avatar = null
    )

    private fun getGroupMenu(menuType: WordListContract.MenuType): List<MenuItem> {
        return when (menuType) {
            WordListContract.MenuType.Enabled -> listOf(
                MenuItem(0, R.string.word_menu_open),
                MenuItem(1, R.string.word_menu_add_word),
                MenuItem(2, R.string.word_menu_flashcards, true),
                MenuItem(3, R.string.word_menu_learn, true),
                MenuItem(4, R.string.word_menu_share, false),
                MenuItem(5, R.string.word_menu_remove)
            )
            WordListContract.MenuType.Disabled -> listOf(
                MenuItem(0, R.string.word_menu_open),
                MenuItem(1, R.string.word_menu_add_word),
                MenuItem(2, R.string.word_menu_flashcards, false),
                MenuItem(3, R.string.word_menu_learn, false),
                MenuItem(4, R.string.word_menu_share, false),
                MenuItem(5, R.string.word_menu_remove)
            )
            WordListContract.MenuType.AllWords -> listOf(
                MenuItem(0, R.string.word_menu_open),
                MenuItem(1, R.string.word_menu_add_word),
                MenuItem(2, R.string.word_menu_flashcards),
                MenuItem(3, R.string.word_menu_learn),
                MenuItem(4, R.string.word_menu_share, false),
                MenuItem(5, R.string.word_menu_remove, false)
            )
        }
    }

    override suspend fun handleEvents(event: WordListContract.Event) {
        when (event) {
            is WordListContract.Event.OnAddWordClick -> {
                setEffect { WordListContract.Effect.Navigation.ToAddWord }
            }
            is WordListContract.Event.OnViewAllClick -> {
                setEffect { WordListContract.Effect.Navigation.ToGroupList }
            }
            is WordListContract.Event.OnImportWordsClick -> {
                setEffect { WordListContract.Effect.Navigation.ToImportWords }
            }
            is WordListContract.Event.OnOnboardingClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    setEffect {
                        WordListContract.Effect.Navigation.ToAddSentence(
                            group = viewState.value.longClickedGroup,
                        )
                    }
                }
            }
            WordListContract.Event.OnMotivationConfirmClick -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                setEffect { WordListContract.Effect.HideBottomSheet }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isSentenceMotivationShown = true
                    setEffect {
                        WordListContract.Effect.Navigation.ToAddSentence(
                            group = viewState.value.longClickedGroup,
                        )
                    }
                }
            }
            WordListContract.Event.OnMotivationDeclineClick -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isSentenceMotivationShown = true
                }

                setEffect { WordListContract.Effect.HideBottomSheet }
            }
            is WordListContract.Event.OnMotivationCancel -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isSentenceMotivationShown = true
                }

                setEffect { WordListContract.Effect.HideBottomSheet }
            }
            is WordListContract.Event.OnWordClick -> {
                setEffect { WordListContract.Effect.ShowUnderConstruction }
            }
            is WordListContract.Event.OnWordDismiss -> {
                deleteWord(event.item)
            }
            is WordListContract.Event.OnGroupClick -> {
                if (currentGroupFlow.value != event.group) {
                    currentGroupFlow.emit(event.group)
                }
            }
            is WordListContract.Event.OnAddGroupClick -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = false) }
                setState { copy(sheetContentType = WordListContract.SheetContentType.AddGroup) }
                setEffect { WordListContract.Effect.ShowBottomSheet }
            }
            is WordListContract.Event.OnApplyGroup -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                saveGroup(groupName)
            }
            is WordListContract.Event.OnGroupAddingCancel -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                setNewGroupName("")
                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.Motivation,
                    )
                }
            }
            is WordListContract.Event.OnGroupLongClick -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = false) }
                setEffect { WordListContract.Effect.ShowBottomSheet }

                handleMenuCreation(event.group)
            }
            is WordListContract.Event.OnGroupMenuItemClick -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                handleMenuItem(event.item)
                setEffect { WordListContract.Effect.HideBottomSheet }
                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.Motivation,
                        longClickedGroup = NoGroup
                    )
                }
            }
            is WordListContract.Event.OnGroupMenuCancel -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                setEffect { WordListContract.Effect.HideBottomSheet }
                setNewGroupName("")
                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.Motivation,
                        longClickedGroup = NoGroup
                    )
                }
            }
            is WordListContract.Event.OnApplyDismiss -> {
                setState { copy(deletedWords = mutableListOf()) }
            }
            is WordListContract.Event.OnRecover -> {
                val mutableDeletedWords = viewState.value.deletedWords
                repository.saveWords(mutableDeletedWords)
            }
            is WordListContract.Event.OnRecovered -> {
                setState { copy(deletedWords = mutableListOf()) }
            }
            is WordListContract.Event.OnProfileClick ->
                setEffect { WordListContract.Effect.Navigation.ToProfile }
        }
    }

    private fun handleMenuCreation(group: GroupListItemsModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (group == NoGroup) {
                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.GroupMenu,
                        longClickedGroup = group,
                        groupMenuList = getGroupMenu(WordListContract.MenuType.AllWords)
                    )
                }
            } else {
                repository.getWordsCountInGroup(group.id)
                    .handle(
                        success = { count ->
                            if (count == 0) {
                                setState {
                                    copy(
                                        sheetContentType = WordListContract.SheetContentType.GroupMenu,
                                        longClickedGroup = group,
                                        groupMenuList = getGroupMenu(WordListContract.MenuType.Disabled)
                                    )
                                }
                            } else {
                                setState {
                                    copy(
                                        sheetContentType = WordListContract.SheetContentType.GroupMenu,
                                        longClickedGroup = group,
                                        groupMenuList = getGroupMenu(WordListContract.MenuType.Enabled)
                                    )
                                }
                            }
                        },
                        error = {
                            setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
                        }
                    )
            }
        }
    }

    private suspend fun handleMenuItem(item: MenuItem) {
        val currentGroup = requireNotNull(viewState.value.longClickedGroup)
        when (item.id) {
            0 -> setEffect { WordListContract.Effect.Navigation.ToGroupDetails(group = currentGroup) }
            1 -> setEffect { WordListContract.Effect.Navigation.ToAddWordWithGroup(group = currentGroup) }
            2 -> setEffect { WordListContract.Effect.Navigation.ToFlashCards(currentGroup) }
            3 -> setEffect { WordListContract.Effect.Navigation.ToAddSentence(currentGroup) }
            4 -> error("Собирай статистику =)")
            5 -> removeGroups(listOf(currentGroup))
            else -> error("there is no such menu element")
        }
    }

    private suspend fun deleteWord(item: WordListItemUI) {
        val mutableDeletedWords = viewState.value.deletedWords
        val wordWithGroups = repository.getWordWithGroups(wordId = item.id).takeOrReturn {
            setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
            return
        }
        mutableDeletedWords.add(wordWithGroups)
        repository.deleteWords(listOf(item.id))
        setState { copy(deletedWords = mutableDeletedWords) }
        setEffect { WordListContract.Effect.ShowRecover }
    }

    private suspend fun showMotivation() {
        val wordsCount = repository.getWordsCountInGroup().takeOrReturn {
            setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
            return
        }

        if (wordsCount == WORDS_FOR_MOTIVATION && !repository.isSentenceMotivationShown) { //todo придумать время мотивации
            delay(3000)
            if (wordsCount % WORDS_FOR_MOTIVATION == 0) {
                setState { copy(sheetContentType = WordListContract.SheetContentType.Motivation) }
                setEffect { WordListContract.Effect.ShowBottomSheet }
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = false) }
            }
        }
    }

    private fun makeMainList(
        filteredList: List<WordListItemUI>,
        groupsList: List<GroupListItemsModel>,
        currentGroup: GroupListItemsModel
    ): MutableList<WordListListModels> {
        val mainList = mutableListOf<WordListListModels>()

        if (showOnboardingLabel(filteredList.size)) {
            mainList.add(OnboardingWordUI)
        }

        mainList.add(
            WordListGroupUI(
                titleRes = R.string.word_group_title,
                buttonRes = R.string.word_group_button,
                groups = groupsList
            )
        )

        mainList.add(WordListDynamicTitleUI(currentGroup = currentGroup))
        if (filteredList.isEmpty()) {
            mainList.add(WordListEmptyUI(descriptionRes = R.string.word_empty_list_description))
        } else {
            mainList.addAll(filteredList)
        }
        return mainList
    }

    private fun showOnboardingLabel(size: Int): Boolean {
        return size > 0
    }

    private fun saveGroup(group: String) {
        val trimGroup = group.trim()
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveGroup(trimGroup)
                .doOnComplete {
                    setNewGroupName("")
                    setState {
                        copy(
                            sheetContentType = WordListContract.SheetContentType.Motivation,
                        )
                    }
                    setEffect { WordListContract.Effect.HideBottomSheet }
                }
        }
    }

    private suspend fun removeGroups(removedGroups: List<GroupListModel>) {
        setState {
            copy(
                sheetContentType = WordListContract.SheetContentType.Motivation,
            )
        }
        currentGroupFlow.emit(NoGroup)
        repository.removeGroups(removedGroups.map { it.id })
            .doOnComplete {
                setEffect { WordListContract.Effect.HideBottomSheet }
            }
    }

    private suspend fun getSavedUser() {
        repository.observeSavedUser()
            .handle(
                success = { userFlow ->
                    userFlow.collect { user ->
                        if (user != null) {
                            setState { copy(avatar = user.avatar) }
                        } else {
                            setState { copy(avatar = null) }
                        }
                    }
                },
                error = {
                    setEffect { WordListContract.Effect.GetUserError(R.string.word_get_user_error) }
                }
            )
    }
}
