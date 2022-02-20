package io.taptap.stupidenglish.features.sentences.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.features.main.ui.MainContract

class SentencesListContract {
    sealed class Event : ViewEvent {
        data class OnShareClick(val sentence: SentencesListItemUI) : Event()
        object OnAddSentenceClick : Event()
        object OnMotivationConfirmClick : Event()
        object OnMotivationDeclineClick : Event()
        object OnMotivationCancel : Event()
        object OnSentenceClick : Event()
    }

    data class State(
        val sentenceList: List<SentencesListListModels> = listOf(),
        val isLoading: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class GetRandomWordsError(val errorRes: Int) : Effect()
        data class GetSentencesError(val errorRes: Int) : Effect()

        object HideMotivation : Effect()
        object ShowMotivation : Effect()
        object ShowUnderConstruction : Effect()

        sealed class Navigation : Effect() {
            data class ToAddSentence(val wordIds: List<Long>) : Navigation()
        }
    }

}