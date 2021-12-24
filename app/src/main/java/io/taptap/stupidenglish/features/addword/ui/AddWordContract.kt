package io.taptap.stupidenglish.features.addword.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState

class AddWordContract {
    sealed class Event : ViewEvent {
        object OnWord : Event()

        data class OnWordChanging(val value: String) : Event()
        data class OnDescriptionChanging(val value: String) : Event()

        object BackToNoneState : AddWordContract.Event()

        object OnSaveWord : Event()

        object OnWaitingDescriptionError : Event()
    }

    data class State(
        val word: String,
        val description: String,
        val addWordState: AddWordState
    ) : ViewState

    enum class AddWordState {
        None,
        HasWord
    }

    sealed class Effect : ViewSideEffect {
        data class SaveError(val errorRes: Int) : Effect()
        data class WaitingForDescriptionError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            object BackToWordList : Navigation()
        }
    }
}