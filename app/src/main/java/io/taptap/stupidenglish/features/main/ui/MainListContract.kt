package io.taptap.stupidenglish.features.main.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract

class MainListContract {
    sealed class Event : ViewEvent {
        object OnAddWordClick : MainListContract.Event()
    }

    data class State(
        val mainList: List<MainListListModels> = listOf(),
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        object DataWasLoaded : Effect()
        data class GetWordsError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            object ToAddWord : Navigation()
        }
    }

}