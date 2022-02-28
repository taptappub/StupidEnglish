package io.taptap.stupidenglish.features.words.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.features.words.ui.model.WordListItemUI
import io.taptap.stupidenglish.features.words.ui.model.WordListListModels

class WordListContract {
    sealed class Event : ViewEvent {
        data class OnWordDismiss(val item: WordListItemUI) : Event()

        object OnAddWordClick : Event()
        object OnOnboardingClick : Event()
        object OnWordClick : Event()

        object OnMotivationConfirmClick : Event()
        object OnMotivationDeclineClick : Event()
        object OnMotivationCancel : Event()

        object OnAddGroupClick : Event()
        data class OnGroupClick(val groupId: Long) : Event()
    }

    data class State(
        val wordList: List<WordListListModels> = listOf(),
        val isLoading: Boolean = false,
        val currentGroup: Group? = null
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class GetRandomWordsError(val errorRes: Int) : Effect()
        data class GetWordsError(val errorRes: Int) : Effect()

        object HideMotivation : Effect()
        object ShowMotivation : Effect()
        object ShowUnderConstruction : Effect()

        sealed class Navigation : Effect() {
            object ToAddWord : Navigation()
            data class ToAddSentence(val wordIds: List<Long>) : Navigation()
        }
    }
}
