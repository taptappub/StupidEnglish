package io.taptap.stupidenglish.features.stack.ui

import com.yuyakaido.android.cardstackview.Direction
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract

class StackContract {
    sealed class Event : ViewEvent {
        data class Swipe(val direction: Direction) : Event()
        object EndSwipe : Event()

        data class OnCardAppeared(val position: Int) : Event()
        data class OnCardDisappeared(val position: Int) : Event()

        object OnNo : Event()
        object OnYes : Event()
    }

    data class State(
        val words: List<Word>,
        val topWordId: Long,
        val swipeState: SwipeState
    ) : ViewState

    sealed class SwipeState {
        data class WasSwiped(val direction: Direction): SwipeState()
        object WasNotSwiped : SwipeState()
    }

    sealed class Effect : ViewSideEffect {
        data class GetWordsError(val errorRes: Int) : Effect()
        data class SaveError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            data class ToAddSentence(val wordIds: List<Long>) : Navigation()
            object BackToSentenceList : Navigation()
        }
    }
}