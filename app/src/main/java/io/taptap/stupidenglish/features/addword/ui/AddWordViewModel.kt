package io.taptap.stupidenglish.features.addword.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.addword.data.AddWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.handle
import javax.inject.Inject

@HiltViewModel
class AddWordViewModel @Inject constructor(
    private val repository: AddWordRepository
) : BaseViewModel<AddWordContract.Event, AddWordContract.State, AddWordContract.Effect>() {

    override fun setInitialState() = AddWordContract.State(
        word = "",
        description = "",
        addWordState = AddWordContract.AddWordState.None
    )

    override fun handleEvents(event: AddWordContract.Event) {
        when (event) {
            is AddWordContract.Event.OnWord ->
                setState { copy(addWordState = AddWordContract.AddWordState.HasWord) }

            is AddWordContract.Event.OnDescriptionChanging ->
                setState { copy(description = event.value) }
            is AddWordContract.Event.OnWordChanging ->
                setState { copy(word = event.value) }

            is AddWordContract.Event.BackToNoneState ->
                setState {
                    copy(
                        addWordState = AddWordContract.AddWordState.None,
                        description = ""
                    )
                }

            is AddWordContract.Event.OnSaveWord -> {
                saveWord()
                setInitialState()
            }
            is AddWordContract.Event.OnWaitingDescriptionError -> setEffect {
                AddWordContract.Effect.WaitingForDescriptionError(
                    R.string.addw_description_not_found_error
                )
            }
        }
    }

    private fun saveWord() {
        val word = viewState.value.word
        val description = viewState.value.description
        viewModelScope.launch {
            repository.saveWord(word, description)
                .handle(
                    success = {
                        withContext(Dispatchers.Main) {
                            setEffect { AddWordContract.Effect.Navigation.BackToWordList }
                        }
                    },
                    error = {
                        withContext(Dispatchers.Main) {
                            setEffect { AddWordContract.Effect.SaveError(R.string.addw_save_error) }
                        }
                    }
                )
        }
    }
}
