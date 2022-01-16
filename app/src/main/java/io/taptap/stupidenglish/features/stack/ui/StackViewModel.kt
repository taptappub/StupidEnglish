package io.taptap.stupidenglish.features.stack.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.addsentence.navigation.AddSentenceArgumentsMapper
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
import io.taptap.stupidenglish.features.stack.data.StackRepository
import io.taptap.stupidenglish.features.words.ui.WordListContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.doOnError
import taptap.pub.handle
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

            setState { copy(words = words, topWordId = words.first().id) }
        }
    }

    override fun setInitialState() =
        StackContract.State(
            words = emptyList(),
            topWordId = 0,
            swipeState = StackContract.SwipeState.WasNotSwiped
        )

    override fun handleEvents(event: StackContract.Event) {
        when (event) {
            is StackContract.Event.Swipe -> {
                setState { copy(swipeState = StackContract.SwipeState.WasSwiped(event.direction)) }
            }
            is StackContract.Event.EndSwipe -> {
                setState { copy(swipeState = StackContract.SwipeState.WasNotSwiped) }
            }
            is StackContract.Event.OnYes -> {
                rememberWord(viewState.value.topWordId)
            }
            is StackContract.Event.OnNo -> {
                noRememberWord(viewState.value.topWordId)
            }
            is StackContract.Event.OnCardAppeared -> {
                val topWordId = viewState.value.words[event.position].id
                setState { copy(topWordId = topWordId) }
            }
            is StackContract.Event.OnCardDisappeared -> {
                if (event.position == viewState.value.words.size - 1) {
                    val wordsIds = viewState.value.words.map { it.id }
                    setEffect { StackContract.Effect.Navigation.ToAddSentence(wordsIds) }
                }
            }
        }
    }

    private fun noRememberWord(wordId: Long) {

    }

    private fun rememberWord(wordId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.rememberWord(wordId)
                .doOnError {
                    withContext(Dispatchers.Main) {
                        setEffect { StackContract.Effect.SaveError(R.string.stck_save_error) }
                    }
                }
        }
    }

}