package io.taptap.stupidenglish.features.addsentence.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.model.Word

class AddSentenceContract {
    sealed class Event : ViewEvent {
        data class OnSaveSentence(val sentence: String) : Event()
    }

    data class State(
        val sentence: String,
        val words: List<Word>
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class SaveError(val errorRes: Int) : Effect()
        data class GetWordsError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            object BackToSentenceList : Navigation()
        }
    }
}