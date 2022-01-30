package io.taptap.stupidenglish.features.main.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceContract
import io.taptap.stupidenglish.features.words.ui.WordListContract

class MainContract {
    sealed class Event : ViewEvent {
        object OnGreetingsClose : Event()
        object OnMotivationConfirmClick : Event()
        object OnMotivationDeclineClick : Event()
        object OnMotivationCancel : Event()
    }

    data class State(
        val pagerIsVisible: Boolean,
        val pageId: Int,
        val isShownGreetings: Boolean = false,
        val timeToShowMotivationToSentence: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        object CloseMotivation : Effect()

        sealed class Navigation : Effect() {
            data class ToAddSentence(val wordIds: List<Long>) : Navigation()
        }
    }
}