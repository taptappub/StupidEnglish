package io.taptap.stupidenglish.features.details.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.sharedmodels.WordItem


class StupidWordContract {
    sealed class Event : ViewEvent

    data class State(
        val category: WordItem?,
        val categoryWordItems: List<WordItem>
    ) : ViewState

    sealed class Effect : ViewSideEffect
}