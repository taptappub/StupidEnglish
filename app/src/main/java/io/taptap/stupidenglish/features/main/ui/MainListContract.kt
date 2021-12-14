package io.taptap.stupidenglish.features.main.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.sharedmodels.WordItem

class MainListContract {
    sealed class Event : ViewEvent {
        data class CategorySelection(val categoryName: String) : Event()
    }

    data class State(
        val mainList: List<MainListListModels> = listOf(),
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        object DataWasLoaded : Effect()

        sealed class Navigation : Effect() {
            data class ToCategoryDetails(val categoryName: String) : Navigation()
        }
    }

}