package io.taptap.stupidenglish.features.main.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.main.data.MainListRepository
import io.taptap.stupidenglish.features.sentences.navigation.SentenceNavigation
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.map
import taptap.pub.takeOrNull
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class MainListViewModel @Inject constructor(
    private val repository: MainListRepository
) : BaseViewModel<MainListContract.Event, MainListContract.State, MainListContract.Effect>() {

    init {
        viewModelScope.launch { getMainList() }
    }

    override fun setInitialState() =
        MainListContract.State(mainList = listOf(), isLoading = true)

    override fun handleEvents(event: MainListContract.Event) {
        when (event) {
            MainListContract.Event.OnAddWordClick -> {
                setEffect { MainListContract.Effect.Navigation.ToAddWord }
            }
            MainListContract.Event.OnOnboardingClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val randomWords = getRandomWords()
                    withContext(Dispatchers.Main) {
                        if (randomWords == null) {
                            setEffect { MainListContract.Effect.GetRandomWordsError(
                                R.string.main_get_random_words_error
                            ) }
                        } else {
                            Log.d("TAGGGG", "navigation")
                            val sentenceNavigation = SentenceNavigation(wordsIds = randomWords)
                            setEffect {
                                MainListContract.Effect.Navigation.ToAddSentence(
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
            setEffect { MainListContract.Effect.GetWordsError(R.string.main_get_list_error) }
        }

        savedWordList.collect {
            val mainList = makeMainList(it.reversed())

            setState {
                copy(mainList = mainList, isLoading = false)
            }
        }
    }

    private fun makeMainList(savedWordList: List<Word>): List<MainListListModels> {
        val mainList = mutableListOf(
            WordListTitleUI(valueRes = R.string.main_list_new_word_title),
            NewWordUI(valueRes = R.string.main_list_add_word),
            WordListTitleUI(valueRes = R.string.main_list_list_title),
        )

        if (showOnboardingLabel()) {
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

    private fun showOnboardingLabel(): Boolean {
        return true //todo доделать
    }
}
