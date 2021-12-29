package io.taptap.stupidenglish.features.words.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.words.data.WordListRepository
import io.taptap.stupidenglish.features.sentences.navigation.SentenceNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.map
import taptap.pub.takeOrNull
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class WordListViewModel @Inject constructor(
    private val repository: WordListRepository
) : BaseViewModel<WordListContract.Event, WordListContract.State, WordListContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) { getMainList() }
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
                            val sentenceNavigation = SentenceNavigation(wordsIds = randomWords)
                            setEffect {
                                WordListContract.Effect.Navigation.ToAddSentence(
                                    sentenceNavigation
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getRandomWords(): List<Long>? {
        return repository.getRandomWords(3)
            .map { list ->
                Log.d("TAGGGG", "list = $list")
                list.map { it.id }
            }
            .takeOrNull()
    }

    private suspend fun getMainList() {
        val savedWordList = repository.getWordList().takeOrReturn {
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
        val mainList = mutableListOf(
            WordListTitleUI(valueRes = R.string.word_list_new_word_title),
            NewWordUI(valueRes = R.string.word_list_add_word),
            WordListTitleUI(valueRes = R.string.word_list_list_title),
        )

        if (showOnboardingLabel(savedWordList.size)) {
            mainList.add(OnboardingWordUI)
        }

        mainList.addAll(savedWordList.map {
            WordListItemUI(
                word = it.word,
                description = it.description
            )
        })

        return mainList
    }

    private fun showOnboardingLabel(size: Int): Boolean {
        return size >= 3
    }
}
