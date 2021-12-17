package io.taptap.stupidenglish.features.main.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.main.data.MainListRepository
import kotlinx.coroutines.launch
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
        }
    }

    private suspend fun getMainList() {
        val savedWordList = repository.getWordList().takeOrReturn {
            setEffect { MainListContract.Effect.GetWordsError(R.string.main_get_list_error) }
        }

        val mainList = mutableListOf(
            WordListTitleUI(valueRes = R.string.main_list_new_word_title),
            NewWordUI(valueRes = R.string.main_list_add_word),
            WordListTitleUI(valueRes = R.string.main_list_list_title),
        ).apply {
            addAll(savedWordList.map {
                WordListItemUI(
                    word = it.word,
                    description = it.description
                )
            })
        }
        setState {
            copy(mainList = mainList, isLoading = false)
        }
        setEffect { MainListContract.Effect.DataWasLoaded }
    }
}
