package io.taptap.stupidenglish.features.sentences.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.share.ShareUtil
import io.taptap.stupidenglish.base.model.Sentence
import io.taptap.stupidenglish.features.sentences.data.SentencesListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.map
import taptap.pub.takeOrNull
import taptap.pub.takeOrReturn
import javax.inject.Inject

private const val SENTENCES_FOR_MOTIVATION = 2

@HiltViewModel
class SentencesListViewModel @Inject constructor(
    private val repository: SentencesListRepository,
    private val shareUtil: ShareUtil
) : BaseViewModel<SentencesListContract.Event, SentencesListContract.State, SentencesListContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) { getSentenceList() }
        viewModelScope.launch(Dispatchers.IO) { motivationShare() }
    }

    override fun setInitialState() =
        SentencesListContract.State(
            sentenceList = listOf(),
            isLoading = true
        )

    override fun handleEvents(event: SentencesListContract.Event) {
        when (event) {
            is SentencesListContract.Event.OnAddSentenceClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val randomWords = getRandomWords()
                    withContext(Dispatchers.Main) {
                        if (randomWords == null) {
                            setEffect { SentencesListContract.Effect.GetRandomWordsError(R.string.stns_get_random_words_error) }
                        } else {
                            setEffect {
                                SentencesListContract.Effect.Navigation.ToAddSentence(randomWords)
                            }
                        }
                    }
                }
            }
            is SentencesListContract.Event.OnShareClick -> {
                //todo добавь какой-то счетчик потом
                shareUtil.share(event.sentence.sentence)
            }
            is SentencesListContract.Event.OnMotivationConfirmClick -> {
                setEffect { SentencesListContract.Effect.HideMotivation }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isShareMotivationShown = true
                }

                val first = viewState.value.sentenceList
                    .filterIsInstance<SentencesListItemUI>()
                    .first()
                shareUtil.share(first.sentence)
            }
            is SentencesListContract.Event.OnMotivationDeclineClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isShareMotivationShown = true
                }
                setEffect { SentencesListContract.Effect.HideMotivation }
            }
            SentencesListContract.Event.OnMotivationCancel ->  {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isShareMotivationShown = true
                }
                setEffect { SentencesListContract.Effect.HideMotivation }
            }
        }
    }

    private suspend fun getRandomWords(): List<Long>? {
        return repository.getRandomWords(3)
            .map { list ->
                list.map { it.id }
            }
            .takeOrNull()
    }

    private suspend fun getSentenceList() {
        val savedSentenceList = repository.getSentenceList().takeOrReturn {
            setEffect { SentencesListContract.Effect.GetSentencesError(R.string.stns_get_sentences_error) }
        }
        savedSentenceList.collect {
            val sentenceList = makeSentenceList(it.reversed())

            setState {
                copy(sentenceList = sentenceList, isLoading = false)
            }
        }
    }

    private fun makeSentenceList(savedSentenceList: List<Sentence>): List<SentencesListListModels> {
        return mutableListOf(
            SentencesListTitleUI(valueRes = R.string.stns_list_new_word_title),
            SentencesListNewSentenceUI(valueRes = R.string.stns_list_add_word),
            SentencesListTitleUI(valueRes = R.string.stns_list_list_title),
        ).apply {
            addAll(savedSentenceList.map {
                SentencesListItemUI(
                    id = it.id,
                    sentence = it.sentence
                )
            })
        }
    }

    private suspend fun motivationShare() {
        if (!repository.isShareMotivationShown) {
            val savedSentenceList = repository.getSentenceList().takeOrReturn {
                setEffect { SentencesListContract.Effect.GetSentencesError(R.string.stns_get_sentences_error) }
            }
            savedSentenceList.collect {
                if (it.size == SENTENCES_FOR_MOTIVATION) {
                    setEffect { SentencesListContract.Effect.ShowMotivation }
                }
            }
        }
    }
}
