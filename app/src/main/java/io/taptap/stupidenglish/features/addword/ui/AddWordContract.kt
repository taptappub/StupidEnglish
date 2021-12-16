package io.taptap.stupidenglish.features.addword.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState

class AddWordContract {
    sealed class Event : ViewEvent {
        data class CategorySelection(val categoryName: String) : Event()
    }

    data class State(
        val mainList: List<String> = listOf(),
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        object DataWasLoaded : Effect()

        sealed class Navigation : Effect() {
            data class ToCategoryDetails(val categoryName: String) : Navigation()
        }
    }

}