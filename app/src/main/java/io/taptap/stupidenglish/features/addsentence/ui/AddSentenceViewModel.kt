package io.taptap.stupidenglish.features.addsentence.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.addsentence.data.AddSentenceRepository
import io.taptap.stupidenglish.features.addsentence.navigation.AddSentenceArgumentsMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.handle
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class AddSentenceViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: AddSentenceRepository,
    private val addSentenceArgumentsMapper: AddSentenceArgumentsMapper
) : BaseViewModel<AddSentenceContract.Event, AddSentenceContract.State, AddSentenceContract.Effect>() {

    private var wordsIdsString: String? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            wordsIdsString = stateHandle.get<String>(NavigationKeys.Arg.SENTENCE_WORDS_ID)
                ?: throw IllegalStateException("No wordsIds was passed to AddSentenceViewModel.")
            val wordsIds = addSentenceArgumentsMapper.mapFrom(wordsIdsString)
            val words = repository.getWordsById(wordsIds!!).takeOrReturn {
                withContext(Dispatchers.Main) {
                    setEffect { AddSentenceContract.Effect.GetWordsError(R.string.adds_get_words_error) }
                }
                return@launch
            }

            setState { copy(words = words) }
        }
    }

    override fun setInitialState() = AddSentenceContract.State(
        sentence = "",
        words = emptyList()
    )

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
                setInitialState()
                val sentence = viewState.value.sentence
                saveSentence(sentence)
            }
            is AddSentenceContract.Event.OnChipClick ->
                setEffect { AddSentenceContract.Effect.ShowUnderConstruction }
        }
    }

    private fun saveSentence(sentence: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val wordsIds = viewState.value.words.map { it.id }
            repository.saveSentence(sentence, wordsIds)
                .handle(
                    success = {
                        withContext(Dispatchers.Main) {
                            setEffect { AddSentenceContract.Effect.Navigation.BackToSentenceList(wordsIdsString) }
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
