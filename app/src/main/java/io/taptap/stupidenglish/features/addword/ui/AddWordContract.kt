package io.taptap.stupidenglish.features.addword.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState

class AddWordContract {
    sealed class Event : ViewEvent {
        data class OnWord(val value: String) : Event()
        data class OnDescription(val value: String) : Event()
        object OnSaveWord : Event()
    }

    data class State(
        val addWordState: AddWordState,
        val word: String,
        val description: String
    ) : ViewState

    enum class AddWordState {
        None,
        HasWord,
        HasDescription
    }

    sealed class Effect : ViewSideEffect {
        data class SaveError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            object BackToWordList : Navigation()
        }
    }
}