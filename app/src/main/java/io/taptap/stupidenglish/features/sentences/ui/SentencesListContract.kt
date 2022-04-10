package io.taptap.stupidenglish.features.sentences.ui

import androidx.compose.material.ExperimentalMaterialApi
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class SentencesListContract {
    sealed class Event : ViewEvent {
        data class OnSentenceDismiss(val item: SentencesListItemUI) : Event()

        data class OnShareClick(val sentence: SentencesListItemUI) : Event()
        data class OnRecovered(val item: SentencesListListModels) : Event()

        object OnAddSentenceClick : Event()
        object OnRecover : Event()
        object OnMotivationConfirmClick : Event()
        object OnMotivationDeclineClick : Event()
        object OnMotivationCancel : Event()
        object OnSentenceClick : Event()
        object OnApplySentenceDismiss : Event()
    }

    data class State @OptIn(ExperimentalMaterialApi::class) constructor(
        val sentenceList: List<SentencesListListModels> = listOf(),
        val isLoading: Boolean,
        val deletedSentenceIds: MutableList<Long>
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class GetRandomWordsError(val errorRes: Int) : Effect()
        data class GetSentencesError(val errorRes: Int) : Effect()

        object HideMotivation : Effect()
        object ShowMotivation : Effect()
        object ShowUnderConstruction : Effect()
        object ShowRecover : Effect()
        data class ChangeBottomBarVisibility(val isShown: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data class ToAddSentence(val wordIds: List<Long>) : Navigation()
        }
    }
}