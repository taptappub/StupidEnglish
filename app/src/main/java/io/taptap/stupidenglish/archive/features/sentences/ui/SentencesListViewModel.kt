package io.taptap.stupidenglish.archive.features.sentences.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.archive.features.sentences.data.SentencesListRepository
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.mapper.toSentenceList
import io.taptap.stupidenglish.base.logic.share.ShareUtil
import io.taptap.stupidenglish.base.model.Sentence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.handle
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
        viewModelScope.launch(Dispatchers.IO) { observeSentenceList() }
        viewModelScope.launch(Dispatchers.IO) { motivationShare() }
    }

    override fun setInitialState() = SentencesListContract.State(
        sentenceList = listOf(),
        isLoading = true,
        deletedSentenceIds = mutableListOf()
    )

    override suspend fun handleEvents(event: SentencesListContract.Event) {
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
                setEffect { SentencesListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
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
                setEffect { SentencesListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isShareMotivationShown = true
                }
                setEffect { SentencesListContract.Effect.HideMotivation }
            }
            is SentencesListContract.Event.OnMotivationCancel -> {
                setEffect { SentencesListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isShareMotivationShown = true
                }
                setEffect { SentencesListContract.Effect.HideMotivation }
            }
            is SentencesListContract.Event.OnSentenceClick -> {
                setEffect { SentencesListContract.Effect.ShowUnderConstruction }
            }
            is SentencesListContract.Event.OnSentenceDismiss -> {
                viewModelScope.launch(Dispatchers.IO) {
                    predeleteSentence(event.item)
                }
            }
            is SentencesListContract.Event.OnApplySentenceDismiss -> {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteSentences()
                }
            }
            is SentencesListContract.Event.OnRecover -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.getSentenceList()
                        .handle(
                            success = {
                                val sentenceList = makeSentenceList(it.reversed())
                                setState {
                                    copy(
                                        sentenceList = sentenceList,
                                        isLoading = false
                                    )
                                }
                            },
                            error = {
                                setEffect { SentencesListContract.Effect.GetSentencesError(R.string.stns_get_sentences_error) }
                                setState { copy(deletedSentenceIds = mutableListOf()) }
                            }
                        )
                }
            }
            is SentencesListContract.Event.OnRecovered -> {
                setState { copy(deletedSentenceIds = mutableListOf()) }
            }
        }
    }

    private suspend fun predeleteSentence(item: SentencesListItemUI) {
        val mutableDeletedSentenceIds = viewState.value.deletedSentenceIds.toMutableList()
        mutableDeletedSentenceIds.add(item.id)
        val list = viewState.value.sentenceList.toMutableList()
        list.remove(item)
        setState { copy(sentenceList = list, deletedSentenceIds = mutableDeletedSentenceIds) }
        setEffect { SentencesListContract.Effect.ShowRecover }
    }

    private suspend fun deleteSentences() {
        deleteSentences(viewState.value.deletedSentenceIds)

        setState { copy(deletedSentenceIds = mutableListOf()) }
    }

    private suspend fun deleteSentences(list: List<Long>) {
        repository.deleteSentences(list)
    }

    private suspend fun getRandomWords(): List<Long>? {
        return repository.getRandomWords(3)
            .map { list ->
                list.map { it.id }
            }
            .takeOrNull()
    }

    private suspend fun observeSentenceList() {
        val savedSentenceList = repository.observeSentenceList().takeOrReturn {
            setEffect { SentencesListContract.Effect.GetSentencesError(R.string.stns_get_sentences_error) }
            return
        }
        savedSentenceList.collect {
            saveListToState(it)
        }
    }

    private fun saveListToState(list: List<Sentence>) {
        val sentenceList = makeSentenceList(list.reversed())

        setState {
            copy(sentenceList = sentenceList, isLoading = false)
        }
    }

    private fun makeSentenceList(savedSentenceList: List<Sentence>): List<SentencesListListModels> {
        val sentenceList = mutableListOf<SentencesListListModels>()

        sentenceList.add(SentencesListTitleUI(valueRes = R.string.stns_list_list_title))
        if (savedSentenceList.isEmpty()) {
            sentenceList.add(SentencesListEmptyUI(descriptionRes = R.string.stns_empty_list_description))
        } else {
            sentenceList.addAll(savedSentenceList.toSentenceList())
        }

        return sentenceList
    }

    private suspend fun motivationShare() {
        if (!repository.isShareMotivationShown) {
            val savedSentenceList = repository.observeSentenceList().takeOrReturn {
                setEffect { SentencesListContract.Effect.GetSentencesError(R.string.stns_get_sentences_error) }
                return
            }
            savedSentenceList.collect {
                if (it.size == SENTENCES_FOR_MOTIVATION) {
                    setEffect { SentencesListContract.Effect.ShowMotivation }
                    setEffect { SentencesListContract.Effect.ChangeBottomBarVisibility(isShown = false) }
                }
            }
        }
    }
}
