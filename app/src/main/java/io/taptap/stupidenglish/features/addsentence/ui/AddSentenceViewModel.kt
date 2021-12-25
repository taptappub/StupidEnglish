package io.taptap.stupidenglish.features.addsentence.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.addsentence.data.AddSentenceRepository
import io.taptap.stupidenglish.features.sentences.navigation.SentenceNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.handle
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class AddSentenceViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: AddSentenceRepository
) : BaseViewModel<AddSentenceContract.Event, AddSentenceContract.State, AddSentenceContract.Effect>() {

    init {
        viewModelScope.launch {
            val wordsIds = stateHandle.get<SentenceNavigation>(NavigationKeys.Arg.SENTENCE_WORDS_ID)
                ?.wordsIds
                ?: throw IllegalStateException("No wordsIds was passed to AddSentenceViewModel.")
            val words = repository.getWordsById(wordsIds).takeOrReturn {
                withContext(Dispatchers.Main) {
                    setEffect { AddSentenceContract.Effect.GetWordsError(R.string.adds_get_words_error) }
                }
            }

            setState { copy(words = words) }
        }
    }

    override fun setInitialState() = AddSentenceContract.State(sentence = "", words = emptyList())

    override fun handleEvents(event: AddSentenceContract.Event) {
        when (event) {
            is AddSentenceContract.Event.OnSentenceChanging ->
                setState { copy(sentence = event.value) }
            is AddSentenceContract.Event.OnWaitingSentenceError -> setEffect {
                AddSentenceContract.Effect.WaitingForSentenceError(
                    R.string.adds_sentence_not_found_error
                )
            }
            is AddSentenceContract.Event.OnSaveSentence -> {
                saveSentence()
                setInitialState()
            }
        }
    }

    private fun saveSentence() {
        val sentence = viewState.value.sentence
        viewModelScope.launch {
            val wordsIds = viewState.value.words.map { it.id }
            repository.saveSentence(sentence, wordsIds)
                .handle(
                    success = {
                        withContext(Dispatchers.Main) {
                            setEffect { AddSentenceContract.Effect.Navigation.BackToSentenceList }
                        }
                    },
                    error = {
                        withContext(Dispatchers.Main) {
                            setEffect { AddSentenceContract.Effect.SaveError(R.string.adds_save_error) }
                        }
                    }
                )
        }
    }
}
