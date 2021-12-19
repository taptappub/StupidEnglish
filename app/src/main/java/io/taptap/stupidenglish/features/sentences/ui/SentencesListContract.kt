package io.taptap.stupidenglish.features.sentences.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.features.sentences.navigation.SentenceNavigation

class SentencesListContract {
    sealed class Event : ViewEvent {
        data class OnShareClick(val sentence: SentencesListItemUI) : Event()
        object OnAddSentenceClick : Event()
    }

    data class State(
        val sentenceList: List<SentencesListListModels> = listOf(),
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class GetRandomWordsError(val errorRes: Int) : Effect()
        data class GetSentencesError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            data class ToAddSentence(val sentenceNavigation: SentenceNavigation) : Navigation()
        }
    }

}