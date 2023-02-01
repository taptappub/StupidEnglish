package io.taptap.stupidenglish.features.stack.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.addsentence.navigation.AddSentenceArgumentsMapper
import io.taptap.stupidenglish.features.stack.data.StackRepository
import io.taptap.stupidenglish.features.stack.ui.adapter.CardStackModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.doOnError
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
            val wordsIdsString = stateHandle.get<String>(NavigationKeys.Arg.WORDS_IDS)
                ?: error("No wordsIds was passed to AddSentenceViewModel.")
            val currentGroupId = stateHandle.get<String>(NavigationKeys.Arg.GROUP_ID)?.toLong()
                ?: error("No group was passed to AddSentenceViewModel.")

            val wordsIds = addSentenceArgumentsMapper.mapFrom(wordsIdsString)
            val words = repository.getWordsById(wordsIds!!).takeOrReturn {
                withContext(Dispatchers.Main) {
                    setEffect { StackContract.Effect.GetWordsError(R.string.adds_get_words_error) }
                }
                return@launch
            }

            setState { copy(words = words.toStackAdapterModels(), topWordId = words.first().id) }
        }
    }

    override fun setInitialState() =
        StackContract.State(
            words = emptyList(),
            swipeState = StackContract.SwipeState.WasNotSwiped,
            topWordId = 0
        )

    override suspend fun handleEvents(event: StackContract.Event) {
        when (event) {
            is StackContract.Event.Swipe -> {
                setState { copy(swipeState = StackContract.SwipeState.WasSwiped) }
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
                    setEffect { StackContract.Effect.Navigation.BackToSentenceList }
                }
            }
            is StackContract.Event.OnBackClick ->
                setEffect { StackContract.Effect.Navigation.BackToSentenceList }
        }
    }

    private fun noRememberWord(wordId: Long) {
        //todo add word to the end of list
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

private fun List<Word>.toStackAdapterModels(): List<CardStackModel> = map {
    CardStackModel(
        id = it.id,
        word = it.word,
        description = it.description,
        points = it.points,
        showDescription = false
    )
}
