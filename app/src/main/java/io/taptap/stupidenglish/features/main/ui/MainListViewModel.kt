package io.taptap.stupidenglish.features.main.ui

import androidx.lifecycle.viewModelScope
import io.taptap.stupidenglish.features.main.data.MainListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import kotlinx.coroutines.launch
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
            is MainListContract.Event.CategorySelection -> {
                setEffect { MainListContract.Effect.Navigation.ToCategoryDetails(event.categoryName) }
            }
        }
    }

    private suspend fun getMainList() {
        val savedWordList = repository.getWordList()
        val mainList = mutableListOf(
            TitleUI(valueRes = R.string.main_list_new_word_title),
            NewWordUI(valueRes = R.string.main_list_add_word),
            TitleUI(valueRes = R.string.main_list_list_title),
        ).apply {
            addAll(savedWordList.map {
                WordListItemUI(
                    id = it.id,
                    word = it.value,
                    description = "some description"
                )
            })
        }
        setState {
            copy(mainList = mainList, isLoading = false)
        }
        setEffect { MainListContract.Effect.DataWasLoaded }
    }
}
