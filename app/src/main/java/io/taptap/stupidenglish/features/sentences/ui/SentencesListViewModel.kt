package io.taptap.stupidenglish.features.sentences.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.sentences.data.SentencesListRepository
import io.taptap.stupidenglish.features.sentences.navigation.SentenceNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.map
import taptap.pub.takeOrNull
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class SentencesListViewModel @Inject constructor(
    private val repository: SentencesListRepository
) : BaseViewModel<SentencesListContract.Event, SentencesListContract.State, SentencesListContract.Effect>() {

    init {
        viewModelScope.launch { getSentenceList() }
    }

    override fun setInitialState() =
        SentencesListContract.State(sentenceList = listOf(), isLoading = true)

    override fun handleEvents(event: SentencesListContract.Event) {
        when (event) {
            is SentencesListContract.Event.OnAddSentenceClick -> {
                viewModelScope.launch {
                    val randomWords = getRandomWords()
                    withContext(Dispatchers.Main) {
                        if (randomWords == null) {
                            setEffect { SentencesListContract.Effect.GetRandomWordsError(R.string.stns_get_random_words_error) }
                        } else {
                            val sentenceNavigation = SentenceNavigation(wordsIds = randomWords)
                            setEffect {
                                SentencesListContract.Effect.Navigation.ToAddSentence(
                                    sentenceNavigation
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getRandomWords(): List<Int>? {
        return repository.getRandomWords(3)
            .map { list ->
                list.map { it.id }
            }
            .takeOrNull()
    }

    private suspend fun getSentenceList() {
        val savedWordList = repository.getSentenceList().takeOrReturn {
            setEffect { SentencesListContract.Effect.GetSentencesError(R.string.stns_get_sentences_error) }
        }
        val mainList = mutableListOf(
            SentencesListTitleUI(valueRes = R.string.stns_list_new_word_title),
            SentencesListNewSentenceUI(valueRes = R.string.stns_list_add_word),
            SentencesListTitleUI(valueRes = R.string.stns_list_list_title),
        ).apply {
            addAll(savedWordList.map {
                SentencesListItemUI(sentence = it.sentence)
            })
        }
        setState {
            copy(sentenceList = mainList, isLoading = false)
        }
        setEffect { SentencesListContract.Effect.DataWasLoaded }
    }
}
