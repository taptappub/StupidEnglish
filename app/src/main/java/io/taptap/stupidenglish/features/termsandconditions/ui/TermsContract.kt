package io.taptap.stupidenglish.features.termsandconditions.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class TermsContract {
    sealed class Event : ViewEvent {
        object OnBackClick : Event()
    }

    data class State(
        val termsUrl: String
    ) : ViewState

    sealed class Effect : ViewSideEffect {

        sealed class Navigation : Effect() {
            object BackToProfile : Navigation()
        }
    }
}