package io.taptap.stupidenglish.features.splash.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class SplashContract {

    sealed class Event : ViewEvent {
        object OnAnimationEnd : Event()
    }

    data class State(
        val list: List<String>,
        val startAnimationDelay: Long,
        val startAnimationDuration: Long,
        val endAnimationDelay: Long
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        sealed class Navigation : Effect() {
            object ToAuthScreen : Navigation()
            object ToWordListScreen : Navigation()
        }
    }
}