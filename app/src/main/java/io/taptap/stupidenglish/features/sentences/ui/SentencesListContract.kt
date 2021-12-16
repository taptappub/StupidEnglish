package io.taptap.stupidenglish.features.sentences.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState

class SentencesListContract {
    sealed class Event : ViewEvent {
        data class CategorySelection(val categoryName: String) : Event()
    }

    data class State(
        val mainList: List<SentencesListListModels> = listOf(),
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        object DataWasLoaded : Effect()

        sealed class Navigation : Effect() {
            data class ToCategoryDetails(val categoryName: String) : Navigation()
        }
    }

}