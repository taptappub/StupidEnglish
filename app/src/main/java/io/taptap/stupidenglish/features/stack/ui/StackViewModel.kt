package io.taptap.stupidenglish.features.stack.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.addsentence.navigation.AddSentenceArgumentsMapper
import io.taptap.stupidenglish.features.stack.data.StackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class StackViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: StackRepository,
    private val addSentenceArgumentsMapper: AddSentenceArgumentsMapper
) : BaseViewModel<StackContract.Event, StackContract.State, StackContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val wordsIdsString = stateHandle.get<String>(NavigationKeys.Arg.WORDS_ID)
                ?: throw IllegalStateException("No wordsIds was passed to AddSentenceViewModel.")
            val wordsIds = addSentenceArgumentsMapper.mapFrom(wordsIdsString)
            val words = repository.getWordsById(wordsIds).takeOrReturn {
                withContext(Dispatchers.Main) {
                    setEffect { StackContract.Effect.GetWordsError(R.string.adds_get_words_error) }
                }
            }

            val stackItems = words.map {
                StackItemUI(word = it.word)
            }

            setState { copy(words = stackItems) }
        }
    }

    override fun setInitialState() =
        StackContract.State(words = emptyList())

    override fun handleEvents(event: StackContract.Event) {
        when (event) {
            is StackContract.Event.OnYesClick -> TODO()
            is StackContract.Event.OnNoClick -> TODO()
            StackContract.Event.OnWordsEnd -> TODO()
        }
    }

}