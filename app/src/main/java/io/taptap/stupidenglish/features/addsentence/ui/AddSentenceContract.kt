package io.taptap.stupidenglish.features.addsentence.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.model.Word

class AddSentenceContract {
    sealed class Event : ViewEvent {
        object OnWaitingSentenceError : Event()
        data class OnSentenceChanging(val value: String) : AddSentenceContract.Event()
        object OnSaveSentence : Event()
        object OnSaveSentenceConfirmed : Event()
        object OnSaveSentenceDeclined : Event()
    }

    data class State(
        val showConfirmSaveDialog: Boolean,
        val sentence: String,
        val words: List<Word>
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class SaveError(val errorRes: Int) : Effect()
        data class GetWordsError(val errorRes: Int) : Effect()
        data class WaitingForSentenceError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            object BackToSentenceList : Navigation()
        }
    }
}