package io.taptap.stupidenglish.features.sentences.ui

import androidx.lifecycle.viewModelScope
import io.taptap.stupidenglish.features.main.data.MainListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SentencesListViewModel @Inject constructor(
    private val repository: MainListRepository
) : BaseViewModel<SentencesListContract.Event, SentencesListContract.State, SentencesListContract.Effect>() {

    init {
        viewModelScope.launch { getMainList() }
    }

    override fun setInitialState() =
        SentencesListContract.State(mainList = listOf(), isLoading = true)

    override fun handleEvents(event: SentencesListContract.Event) {
        when (event) {
            is SentencesListContract.Event.CategorySelection -> {
                setEffect { SentencesListContract.Effect.Navigation.ToCategoryDetails(event.categoryName) }
            }
        }
    }

    private suspend fun getMainList() {
        val savedWordList = repository.getWordList()
        val mainList = mutableListOf(
            SentencesListTitleUI(valueRes = R.string.stns_list_new_word_title),
            SentencesListNewSentenceUI(valueRes = R.string.stns_list_add_word),
            SentencesListTitleUI(valueRes = R.string.stns_list_list_title),
        ).apply {
            addAll(savedWordList.map {
                SentencesListItemUI(
                    id = it.id,
                    sentence = it.value
                )
            })
        }
        setState {
            copy(mainList = mainList, isLoading = false)
        }
        setEffect { SentencesListContract.Effect.DataWasLoaded }
    }
}
