package io.taptap.stupidenglish.features.stack.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.stack.ui.adapter.CardStackModel

class StackContract {
    sealed class Event : ViewEvent {
        object Swipe : Event()
        object EndSwipe : Event()

        data class OnCardAppeared(val position: Int) : Event()
        data class OnCardDisappeared(val position: Int) : Event()

        object OnNo : Event()
        object OnYes : Event()

        object OnBackClick : Event()
    }

    data class State(
        val swipeState: SwipeState,
        val words: List<CardStackModel>,
        val topWordId: Long
    ) : ViewState

    sealed class SwipeState {
        object WasSwiped : SwipeState()
        object WasNotSwiped : SwipeState()
    }

    sealed class Effect : ViewSideEffect {
        data class GetWordsError(val errorRes: Int) : Effect()
        data class SaveError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            object BackToSentenceList : Navigation()
        }
    }
}