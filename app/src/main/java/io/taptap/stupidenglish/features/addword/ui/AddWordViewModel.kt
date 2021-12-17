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

    private var word: String = ""
    private var description: String = ""

    override fun setInitialState() =
        AddWordContract.State(addWordState = AddWordContract.AddWordState.None)

    override fun handleEvents(event: AddWordContract.Event) {
        when (event) {
            is AddWordContract.Event.OnWord -> setWord(event.value)
            is AddWordContract.Event.OnDescription -> setDescription(event.value)
            is AddWordContract.Event.OnSaveWord -> saveWord()
        }
    }

    private fun setWord(value: String) {
        word = value
        setState {
            copy(addWordState = AddWordContract.AddWordState.HasWord)
        }
    }

    private fun setDescription(value: String) {
        description = value
        setState {
            copy(addWordState = AddWordContract.AddWordState.HasDescription)
        }
    }

    private fun saveWord() {
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
