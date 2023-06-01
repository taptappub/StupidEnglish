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
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.Word
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import taptap.pub.doOnComplete
import taptap.pub.handle
import taptap.pub.takeOrNull
import taptap.pub.takeOrReturn
import javax.inject.Inject

private const val WORDS_FOR_MOTIVATION = 1

@HiltViewModel
class WordListViewModel @Inject constructor(
    private val repository: WordListRepository
) : BaseViewModel<WordListContract.Event, WordListContract.State, WordListContract.Effect>() {

    private lateinit var words: List<WordListItemUI>
    private lateinit var groups: List<GroupListItemsModel>

    var groupName by mutableStateOf("")
        private set

    fun setNewGroupName(newGroupName: String) {
        groupName = newGroupName
    }

    init {
        viewModelScope.launch(Dispatchers.IO) { getMainList() }
        viewModelScope.launch(Dispatchers.IO) { showMotivation() }
        viewModelScope.launch(Dispatchers.IO) { getSavedUser() }
    }

    override fun setInitialState() = WordListContract.State(
        wordList = listOf(),
        isLoading = true,
        groupMenuList = getGroupMenu(WordListContract.MenuType.Enabled),
        longClickedGroup = NoGroup,
        currentGroup = NoGroup,
        sheetContentType = WordListContract.SheetContentType.Motivation,
        deletedWordIds = mutableListOf(),
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
                predeleteWord(event.item)
            }
            is WordListContract.Event.OnGroupClick -> {
                if (viewState.value.currentGroup != event.group) {
                    makeMainList(words, groups, event.group)
                    setState { copy(currentGroup = event.group) }
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
                deleteWords()
            }
            is WordListContract.Event.OnRecover -> {
                val words = repository.getWordList().takeOrReturn {
                    setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
                    setState { copy(deletedWordIds = mutableListOf()) }
                    return
                }

                val groups = repository.getGroupList().takeOrReturn {
                    setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
                    setState { copy(deletedWordIds = mutableListOf()) }
                    return
                }

                makeMainListWithMap(words.reversed(), groups.reversed())
            }
            is WordListContract.Event.OnRecovered -> {
                setState { copy(deletedWordIds = mutableListOf()) }
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

    private suspend fun predeleteWord(item: WordListItemUI) {
        val mutableDeletedWordIds = viewState.value.deletedWordIds.toMutableList()
        mutableDeletedWordIds.add(item.id)
        val list = viewState.value.wordList.toMutableList()
        list.remove(item)
        setState { copy(wordList = list, deletedWordIds = mutableDeletedWordIds) }
        setEffect { WordListContract.Effect.ShowRecover }
    }

    private suspend fun deleteWords() {
        repository.deleteWords(viewState.value.deletedWordIds)
        setState { copy(deletedWordIds = mutableListOf()) }
    }

    private fun filterWordListByGroup(
        words: List<WordListItemUI>,
        group: GroupListModel
    ): List<WordListItemUI> {
        return if (group == NoGroup) {
            words
        } else {
            words.filter {
                it.groupsIds.contains(group.id)
            }
        }
    }

    private suspend fun showMotivation() {
        val wordsCountFlow = repository.observeWordList().takeOrNull()

        wordsCountFlow?.collect { words ->
            val size = words.size

            if (size == WORDS_FOR_MOTIVATION && !repository.isSentenceMotivationShown) { //todo придумать время мотивации
                delay(3000)
                if (size % WORDS_FOR_MOTIVATION == 0) {
                    setState { copy(sheetContentType = WordListContract.SheetContentType.Motivation) }
                    setEffect { WordListContract.Effect.ShowBottomSheet }
                    setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = false) }
                }
            }
        }
    }

    private suspend fun getMainList() {
        val savedWordList = repository.observeWordList().takeOrReturn {
            setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
            return
        }
        val groupList = repository.observeGroupList().takeOrReturn {
            setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_groups_error) }
            return
        }

        savedWordList.combine(groupList) { words, groups ->
            val mainList = words.reversed()
            val groupsList = groups.reversed()
            Pair(mainList, groupsList)
        }.collect { pair ->
            val mainList = pair.first
            val groupsList = pair.second

            makeMainListWithMap(mainList, groupsList)
        }
    }

    private fun makeMainListWithMap(mainList: List<Word>, groupsList: List<Group>) {
        words = mainList.toWordsList()
        groups = groupsList.toGroupsList(withNoGroup = true)
        val group = try {
            viewState.value.currentGroup
        } catch (e: Exception) {
            e.printStackTrace()
            NoGroup
        }

        makeMainList(words, groups, group)
    }

    private fun makeMainList(
        list: List<WordListItemUI>,
        groupsList: List<GroupListItemsModel>,
        currentGroup: GroupListItemsModel
    ) {
        val filteredList = filterWordListByGroup(list, currentGroup)

        val mainList = mutableListOf<WordListListModels>()
        if (showOnboardingLabel(words.size)) {
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

        setState {
            copy(wordList = mainList, isLoading = false)
        }
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
                currentGroup = NoGroup,
            )
        }
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
