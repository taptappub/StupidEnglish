package io.taptap.stupidenglish.features.sentences.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class SentencesListContract {
    sealed class Event : ViewEvent {
        data class OnShareClick(val sentence: SentencesListItemUI) : Event()
        object OnAddSentenceClick : Event()
        object OnMotivationDeclineClick : Event()
        object OnMotivationConfirmClick : Event()
    }

    data class State(
        val sentenceList: List<SentencesListListModels> = listOf(),
        val isLoading: Boolean,
        val timeToShowMotivationToSharing: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class GetRandomWordsError(val errorRes: Int) : Effect()
        data class GetSentencesError(val errorRes: Int) : Effect()
        object CloseMotivation : Effect()

        sealed class Navigation : Effect() {
            data class ToAddSentence(val wordIds: List<Long>) : Navigation()
        }
    }

}