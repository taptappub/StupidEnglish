package io.taptap.stupidenglish.features.addsentence.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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
            is AddSentenceContract.Event.OnSaveSentence -> saveSentence(event.sentence)
        }
    }

    private fun saveSentence(sentence: String) {
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
