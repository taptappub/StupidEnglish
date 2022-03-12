package io.taptap.stupidenglish.features.words.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
import io.taptap.stupidenglish.features.words.ui.model.GroupListModels
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
        object OnApplyGroup : Event()
        object OnGroupAddingCancel : Event()
        data class OnGroupChanging(val value: String) : Event()
        data class OnGroupClick(val group: GroupListModels) : Event()
    }

    data class State(
        val wordList: List<WordListListModels>,
        val isLoading: Boolean = false,
        val currentGroup: GroupListModels,
        val sheetContentType: SheetContentType,
        val group: String
    ) : ViewState

    enum class SheetContentType {
        AddGroup,
        Motivation
    }

    sealed class Effect : ViewSideEffect {
        data class GetRandomWordsError(val errorRes: Int) : Effect()
        data class GetWordsError(val errorRes: Int) : Effect()

        object HideBottomSheet : Effect()
        object ShowBottomSheet : Effect()
        object ShowUnderConstruction : Effect()
        data class ChangeBottomBarVisibility(val isShown: Boolean) : Effect()

        sealed class Navigation : Effect() {
            object ToAddWord : Navigation()
            data class ToAddSentence(val wordIds: List<Long>) : Navigation()
        }
    }
}
