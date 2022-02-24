package io.taptap.stupidenglish.features.words.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.words.data.WordListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.map
import taptap.pub.takeOrNull
import taptap.pub.takeOrReturn
import javax.inject.Inject

private const val WORDS_FOR_MOTIVATION = 1

@HiltViewModel
class WordListViewModel @Inject constructor(
    private val repository: WordListRepository
) : BaseViewModel<WordListContract.Event, WordListContract.State, WordListContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) { getMainList() }
        viewModelScope.launch(Dispatchers.IO) { showMotivation() }
    }

    override fun setInitialState() =
        WordListContract.State(wordList = listOf(), isLoading = true)

    override fun handleEvents(event: WordListContract.Event) {
        when (event) {
            WordListContract.Event.OnAddWordClick -> {
                setEffect { WordListContract.Effect.Navigation.ToAddWord }
            }
            WordListContract.Event.OnOnboardingClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val randomWords = getRandomWords()
                    withContext(Dispatchers.Main) {
                        if (randomWords == null) {
                            setEffect { WordListContract.Effect.GetRandomWordsError(
                                R.string.word_get_random_words_error
                            ) }
                        } else {
                            setEffect {
                                WordListContract.Effect.Navigation.ToAddSentence(randomWords)
                            }
                        }
                    }
                }
            }
            WordListContract.Event.OnMotivationConfirmClick -> {
                setEffect { WordListContract.Effect.HideMotivation }
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
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isSentenceMotivationShown = true
                }

                setEffect { WordListContract.Effect.HideMotivation }
            }
            WordListContract.Event.OnMotivationCancel -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isSentenceMotivationShown = true
                }

                setEffect { WordListContract.Effect.HideMotivation }
            }
            WordListContract.Event.OnWordClick -> {
                setEffect { WordListContract.Effect.ShowUnderConstruction }
            }
            is WordListContract.Event.OnWordDismiss -> {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteWord(event.item)
                }
            }
        }
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
                    setEffect { WordListContract.Effect.ShowMotivation }
                }
            }
        }
    }

    private suspend fun getMainList() {
        val savedWordList = repository.observeWordList().takeOrReturn {
            setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
        }

        savedWordList.collect {
            val mainList = makeMainList(it.reversed())

            setState {
                copy(wordList = mainList, isLoading = false)
            }
        }
    }

    private fun makeMainList(savedWordList: List<Word>): List<WordListListModels> {
        val mainList = mutableListOf<WordListListModels>()
        if (showOnboardingLabel(savedWordList.size)) {
            mainList.add(OnboardingWordUI)
        }

        mainList.add(WordListTitleUI(valueRes = R.string.word_list_list_title))
        mainList.addAll(savedWordList.map {
            WordListItemUI(
                id = it.id,
                word = it.word,
                description = it.description
            )
        })

        return mainList
    }

    private fun showOnboardingLabel(size: Int): Boolean {
        return size > 0
    }
}
