package io.taptap.stupidenglish.features.words.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
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
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListItemsModels
import io.taptap.uikit.group.GroupListModels
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.doOnComplete
import taptap.pub.handle
import taptap.pub.map
import taptap.pub.takeOrNull
import taptap.pub.takeOrReturn
import javax.inject.Inject

private const val WORDS_FOR_MOTIVATION = 1

@HiltViewModel
class WordListViewModel @Inject constructor(
    private val repository: WordListRepository
) : BaseViewModel<WordListContract.Event, WordListContract.State, WordListContract.Effect>() {

    private lateinit var words: List<WordListItemUI>
    private lateinit var groups: List<GroupListItemsModels>

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
        groupMenuList = listOf(
            MenuItem(0, R.string.word_menu_open),
            MenuItem(1, R.string.word_menu_add_word),
            MenuItem(2, R.string.word_menu_flashcards),
            MenuItem(3, R.string.word_menu_learn),
            MenuItem(4, R.string.word_menu_remove)
        ),
        longClickedGroup = null,
        currentGroup = NoGroup,
        sheetContentType = WordListContract.SheetContentType.Motivation,
        deletedWordIds = mutableListOf(),
        avatar = null
    )

    override suspend fun handleEvents(event: WordListContract.Event) {
        when (event) {
            is WordListContract.Event.OnAddWordClick -> {
                setEffect { WordListContract.Effect.Navigation.ToAddWord }
            }
            is WordListContract.Event.OnImportWordsClick -> {
                setEffect { WordListContract.Effect.Navigation.ToImportWords }
            }
            is WordListContract.Event.OnOnboardingClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val randomWords = getRandomWords(viewState.value.currentGroup.id)
                    withContext(Dispatchers.Main) {
                        if (randomWords == null) {
                            setEffect {
                                WordListContract.Effect.GetRandomWordsError(
                                    R.string.word_get_random_words_error
                                )
                            }
                        } else {
                            setEffect {
                                WordListContract.Effect.Navigation.ToAddSentence(
                                    group = viewState.value.currentGroup,
                                    wordIds = randomWords
                                )
                            }
                        }
                    }
                }
            }
            WordListContract.Event.OnMotivationConfirmClick -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                setEffect { WordListContract.Effect.HideBottomSheet }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isSentenceMotivationShown = true

                    val randomWords = getRandomWords(viewState.value.currentGroup.id)
                    withContext(Dispatchers.Main) {
                        if (randomWords != null) {
//                            setEffect {
//                                WordListContract.Effect.Navigation.ToAddSentence(randomWords)
//                            }
                        }
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
            is WordListContract.Event.OnGroupMenuCancel -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                setNewGroupName("")
                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.Motivation,
                    )
                }
            }
            is WordListContract.Event.OnWordClick -> {
                setEffect { WordListContract.Effect.ShowUnderConstruction }
            }
            is WordListContract.Event.OnWordDismiss -> {
                viewModelScope.launch(Dispatchers.IO) {
                    predeleteWord(event.item)
                }
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

                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.GroupMenu,
                        longClickedGroup = event.group
                    )
                }
            }
            is WordListContract.Event.OnGroupMenuItemClick -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                handleMenuItem(event.item)
                setEffect { WordListContract.Effect.HideBottomSheet }
                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.Motivation,
                        longClickedGroup = null
                    )
                }
            }
            is WordListContract.Event.OnGroupMenuCancel -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                setEffect { WordListContract.Effect.HideBottomSheet }
                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.Motivation,
                        longClickedGroup = null
                    )
                }
            }
            is WordListContract.Event.OnApplySentenceDismiss -> {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteWords()
                }
            }
            is WordListContract.Event.OnRecover -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val words = repository.getWordList().takeOrReturn {
                        setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
                        setState { copy(deletedWordIds = mutableListOf()) }
                        return@launch
                    }

                    val groups = repository.getGroupList().takeOrReturn {
                        setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
                        setState { copy(deletedWordIds = mutableListOf()) }
                        return@launch
                    }

                    makeMainListWithMap(words.reversed(), groups.reversed())
                }
            }
            is WordListContract.Event.OnRecovered -> {
                setState { copy(deletedWordIds = mutableListOf()) }
            }
            is WordListContract.Event.OnProfileClick ->
                setEffect { WordListContract.Effect.Navigation.ToProfile }
        }
    }

    private fun handleMenuItem(item: MenuItem) {
        val currentGroup = requireNotNull(viewState.value.longClickedGroup)
        when (item.id) {
            0 -> setEffect { WordListContract.Effect.Navigation.ToGroupDetails(group = currentGroup) }
            1 -> setEffect { WordListContract.Effect.Navigation.ToAddWordWithGroup(group = currentGroup) }
            2 -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val randomWords = getRandomWords(currentGroup.id)
                    withContext(Dispatchers.Main) {
                        if (randomWords == null) {
                            setEffect {
                                WordListContract.Effect.GetRandomWordsError(
                                    R.string.word_get_random_words_error
                                )
                            }
                        } else {
                            setEffect {
                                WordListContract.Effect.Navigation.ToFlashCards(
                                    group = currentGroup,
                                    wordIds = randomWords
                                )
                            }
                        }
                    }
                }
            }
            3 -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val randomWords = getRandomWords(currentGroup.id)
                    withContext(Dispatchers.Main) {
                        if (randomWords == null) {
                            setEffect {
                                WordListContract.Effect.GetRandomWordsError(
                                    R.string.word_get_random_words_error
                                )
                            }
                        } else {
                            setEffect {
                                WordListContract.Effect.Navigation.ToAddSentence(
                                    currentGroup,
                                    randomWords
                                )
                            }
                        }
                    }
                }
            }
            4 -> removeGroups(listOf(currentGroup))
            else -> error("there is no such menu element")
            /* MenuItem(0, R.string.word_menu_open),
             MenuItem(1, R.string.word_menu_add_word),
             MenuItem(2, R.string.word_menu_flashcards),
             MenuItem(3, R.string.word_menu_learn),
             MenuItem(4, R.string.word_menu_remove)*/

//            data class ToGroupDetails(val group: GroupListItemsModels) : WordListContract.Effect.Navigation()
//            data class ToAddWordWithGroup(val group: GroupListItemsModels) : WordListContract.Effect.Navigation()
//            data class ToFlashCards(val group: GroupListItemsModels) : WordListContract.Effect.Navigation()
//            data class ToAddSentence(val group: GroupListItemsModels, val wordIds: List<Long>) : WordListContract.Effect.Navigation()
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
        deleteWords(viewState.value.deletedWordIds)

        setState { copy(deletedWordIds = mutableListOf()) }
    }

    private suspend fun deleteWords(list: List<Long>) {
        repository.deleteWords(list)
    }

    private fun filterWordListByGroup(
        words: List<WordListItemUI>,
        group: GroupListModels
    ): List<WordListItemUI> {
        return if (group == NoGroup) {
            words
        } else {
            words.filter {
                it.groupsIds.contains(group.id)
            }
        }
    }

    private suspend fun getRandomWords(
        groupId: Long
    ): List<Long>? {
        return repository.getRandomWords(3, groupId)
            .map { list ->
                list.map { it.id }
            }
            .takeOrNull()
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
        words = mainList.map {
            WordListItemUI(
                id = it.id,
                word = it.word,
                description = it.description,
                groupsIds = it.groupsIds
            )
        }
        groups = makeGroupsList(groupsList)
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
        groupsList: List<GroupListItemsModels>,
        currentGroup: GroupListItemsModels
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

    private fun makeGroupsList(groupsList: List<Group>): List<GroupListItemsModels> {
        val groupList = mutableListOf<GroupListItemsModels>()
        groupList.add(NoGroup)
        groupList.addAll(groupsList.map {
            GroupItemUI(
                id = it.id,
                name = it.name
            )
        })

        return groupList
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

    private fun removeGroups(removedGroups: List<GroupListModels>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeGroups(removedGroups.map { it.id })
                .doOnComplete {
                    withContext(Dispatchers.Main) {
                        setState {
                            copy(
                                sheetContentType = WordListContract.SheetContentType.Motivation,
                            )
                        }
                        setEffect { WordListContract.Effect.HideBottomSheet }
                    }
                }
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
