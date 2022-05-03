package io.taptap.stupidenglish.features.words.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.sources.groups.read.GroupItemUI
import io.taptap.stupidenglish.base.logic.sources.groups.read.GroupListModels
import io.taptap.stupidenglish.base.logic.sources.groups.read.NoGroup
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.words.data.WordListRepository
import io.taptap.stupidenglish.features.words.ui.model.OnboardingWordUI
import io.taptap.stupidenglish.features.words.ui.model.WordListEmptyUI
import io.taptap.stupidenglish.features.words.ui.model.WordListGroupUI
import io.taptap.stupidenglish.features.words.ui.model.WordListItemUI
import io.taptap.stupidenglish.features.words.ui.model.WordListListModels
import io.taptap.stupidenglish.features.words.ui.model.WordListTitleUI
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
    private lateinit var groups: List<GroupListModels>

    init {
        viewModelScope.launch(Dispatchers.IO) { getMainList() }
        viewModelScope.launch(Dispatchers.IO) { showMotivation() }
    }

    override fun setInitialState() = WordListContract.State(
        wordList = listOf(),
        isLoading = true,
        group = "",
        dialogGroups = listOf(),
        removedGroups = listOf(),
        currentGroup = NoGroup,
        sheetContentType = WordListContract.SheetContentType.Motivation,
        deletedWordIds = mutableListOf()
    )

    override suspend fun handleEvents(event: WordListContract.Event) {
        when (event) {
            WordListContract.Event.OnAddWordClick -> {
                setEffect { WordListContract.Effect.Navigation.ToAddWord }
            }
            WordListContract.Event.OnOnboardingClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val randomWords = getRandomWords()
                    withContext(Dispatchers.Main) {
                        if (randomWords == null) {
                            setEffect {
                                WordListContract.Effect.GetRandomWordsError(
                                    R.string.word_get_random_words_error
                                )
                            }
                        } else {
                            setEffect {
                                WordListContract.Effect.Navigation.ToAddSentence(randomWords)
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

                    val randomWords = getRandomWords()
                    withContext(Dispatchers.Main) {
                        if (randomWords != null) {
                            setEffect {
                                WordListContract.Effect.Navigation.ToAddSentence(randomWords)
                            }
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
            is WordListContract.Event.OnGroupRemovingCancel -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.Motivation,
                        group = ""
                    )
                }
            }
            is WordListContract.Event.OnWordClick -> {
                setEffect { WordListContract.Effect.ShowUnderConstruction }
            }
            is WordListContract.Event.OnWordDismiss -> {
                viewModelScope.launch(Dispatchers.IO) {
                    predeleteSentence(event.item)
                }
            }
            is WordListContract.Event.OnGroupClick -> {
                if (viewState.value.currentGroup != event.group) {
                    filterWordListByGroup(event.group)
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
                saveGroup(viewState.value.group)
            }
            is WordListContract.Event.OnGroupChanging -> {
                setState { copy(group = event.value) }
            }
            is WordListContract.Event.OnGroupAddingCancel -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                setState {
                    copy(
                        sheetContentType = WordListContract.SheetContentType.Motivation,
                        group = ""
                    )
                }
            }
            is WordListContract.Event.OnGroupLongClick -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = false) }
                setEffect { WordListContract.Effect.ShowBottomSheet }

                val selectedGroups = ArrayList(viewState.value.removedGroups)
                selectedGroups.add(event.group)

                setState {
                    copy(
                        removedGroups = selectedGroups,
                        sheetContentType = WordListContract.SheetContentType.RemoveGroup
                    )
                }
            }
            is WordListContract.Event.OnGroupSelect -> {
                val selectedGroups = ArrayList(viewState.value.removedGroups)
                if (selectedGroups.contains(event.item)) {
                    selectedGroups.remove(event.item)
                } else {
                    selectedGroups.add(event.item)
                }
                setState { copy(removedGroups = selectedGroups) }
            }
            is WordListContract.Event.OnApplyGroupsRemove -> {
                setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                removeGroups(viewState.value.removedGroups)
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
        }
    }

    private suspend fun predeleteSentence(item: WordListItemUI) {
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

    private fun filterWordListByGroup(group: GroupListModels) {
        val list = if (group == NoGroup) {
            words
        } else {
            words.filter {
                it.groupsIds.contains(group.id)
            }
        }
        makeMainList(list, groups)
    }

    private suspend fun deleteWord(item: WordListItemUI) {
        repository.deleteWord(item.id)
    }

    private suspend fun getRandomWords(): List<Long>? {
        return repository.getRandomWords(3)
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
                delay(2000)
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

        makeMainList(words, groups)
    }

    private fun makeMainList(
        list: List<WordListItemUI>,
        groupsList: List<GroupListModels>
    ) {
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

        mainList.add(WordListTitleUI(valueRes = R.string.word_list_list_title))
        if (list.isEmpty()) {
            mainList.add(WordListEmptyUI(descriptionRes = R.string.word_empty_list_description))
        } else {
            mainList.addAll(list)
        }

        val dialogGroups = groupsList.toMutableList().apply {
            remove(NoGroup)
        }

        setState {
            copy(wordList = mainList, isLoading = false, dialogGroups = dialogGroups)
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

    private fun showOnboardingLabel(size: Int): Boolean {
        return size > 0
    }

    private fun saveGroup(group: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveGroup(group)
                .doOnComplete {
                    setState {
                        copy(
                            sheetContentType = WordListContract.SheetContentType.Motivation,
                            group = ""
                        )
                    }
                    setEffect { WordListContract.Effect.HideBottomSheet }
                }
        }
    }

    private fun removeGroups(removedGroups: List<GroupListModels>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeGroups(removedGroups.map { it.id })
                .handle(
                    success = {
                        withContext(Dispatchers.Main) {
                            setState {
                                copy(
                                    sheetContentType = WordListContract.SheetContentType.Motivation,
                                    removedGroups = emptyList()
                                )
                            }
                            setEffect { WordListContract.Effect.HideBottomSheet }
                        }
                    },
                    error = {
                        withContext(Dispatchers.Main) {
                            setState {
                                copy(
                                    sheetContentType = WordListContract.SheetContentType.Motivation,
                                    removedGroups = emptyList()
                                )
                            }
                            setEffect { WordListContract.Effect.HideBottomSheet }
                        }
                    }
                )
        }
    }
}
