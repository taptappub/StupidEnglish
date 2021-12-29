package io.taptap.stupidenglish.features.main.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class MainContract {
    sealed class Event : ViewEvent {
        object OnGreetingsClose : Event()
    }

    data class State(
        val pagerIsVisible: Boolean,
        val pageId: Int,
        val isShownGreetings: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        sealed class Navigation : Effect() {
        }
    }
}