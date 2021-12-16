package io.taptap.stupidenglish.features.addword.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.addword.data.AddWordRepository
import javax.inject.Inject

@HiltViewModel
class AddWordViewModel @Inject constructor(
    private val repository: AddWordRepository
) : BaseViewModel<AddWordContract.Event, AddWordContract.State, AddWordContract.Effect>() {

    override fun setInitialState() =
        AddWordContract.State(mainList = listOf(), isLoading = true)

    override fun handleEvents(event: AddWordContract.Event) {
        when (event) {
            is AddWordContract.Event.CategorySelection -> {
                setEffect { AddWordContract.Effect.Navigation.ToCategoryDetails(event.categoryName) }
            }
        }
    }

    private suspend fun getMainList() {
        val savedWordList = repository.getWordList()

        setState {
            copy(mainList = mainList, isLoading = false)
        }
        setEffect { AddWordContract.Effect.DataWasLoaded }
    }
}
