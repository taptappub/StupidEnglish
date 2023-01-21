package io.taptap.stupidenglish.features.addsentence.ui

import androidx.compose.runtime.MutableState
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.model.Word

class AddSentenceContract {
    sealed class Event : ViewEvent {
        object OnWaitingSentenceError : Event()
        object OnSaveSentence : Event()
        data class OnChipClick(val word: Word) : Event()
        object OnBackClick : Event()
    }

    data class State(
        val words: List<Word>
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class SaveError(val errorRes: Int) : Effect()
        data class GetWordsError(val errorRes: Int) : Effect()
        data class WaitingForSentenceError(val errorRes: Int) : Effect()

        data class ShowDescription(val description: String) : Effect()

        sealed class Navigation : Effect() {
            data class BackToSentenceList(val ids: String?) : Navigation()
        }
    }
}