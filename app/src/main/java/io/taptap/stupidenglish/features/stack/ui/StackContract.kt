package io.taptap.stupidenglish.features.stack.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class StackContract {
    sealed class Event : ViewEvent {
        data class OnNoClick(val wordId: Long) : Event()
        data class OnYesClick(val wordId: Long) : Event()
        object OnWordsEnd : Event()
    }

    data class State(
        val words: List<StackModels>
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class GetWordsError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            object BackToSentenceList : Navigation()
        }
    }
}