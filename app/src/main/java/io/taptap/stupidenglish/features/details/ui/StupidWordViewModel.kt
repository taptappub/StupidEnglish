package io.taptap.stupidenglish.features.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.details.data.StupidWordRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StupidWordViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: StupidWordRepository
) : BaseViewModel<
        StupidWordContract.Event,
        StupidWordContract.State,
        StupidWordContract.Effect>() {

    init {
        viewModelScope.launch {
            val categoryId = stateHandle.get<String>(NavigationKeys.Arg.WORD_ID)
                ?: throw IllegalStateException("No wordId was passed to destination.")
            val categories = repository.getWordList()
            val category = categories.first { it.id == categoryId }
            setState { copy(category = category) }

            val foodItems = repository.getMealsByCategory(categoryId)
            setState { copy(categoryWordItems = foodItems) }
        }
    }

    override fun setInitialState() = StupidWordContract.State(null, listOf())

    override fun handleEvents(event: StupidWordContract.Event) {}

}
