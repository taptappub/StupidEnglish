package io.taptap.stupidenglish.features.importwordstutorial.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class ImportWordsTutorialContract {
    sealed class Event : ViewEvent

    data class State(
        val link: String,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        sealed class Navigation : Effect() {
            object BackToImportWords : Navigation()
        }
    }
}