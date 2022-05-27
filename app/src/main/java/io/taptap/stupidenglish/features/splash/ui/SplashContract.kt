package io.taptap.stupidenglish.features.splash.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class SplashContract {
    class Event : ViewEvent

    class State : ViewState

    sealed class Effect : ViewSideEffect {
        sealed class Navigation : Effect() {
            object ToAuthScreen : Navigation()
            object ToWordListScreen : Navigation()
        }
    }
}