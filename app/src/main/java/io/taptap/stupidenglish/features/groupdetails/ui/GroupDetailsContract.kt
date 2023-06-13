package io.taptap.stupidenglish.features.groupdetails.ui

import androidx.annotation.StringRes
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.model.WordWithGroups
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsWordItemUI
import io.taptap.uikit.group.GroupListItemsModel

class GroupDetailsContract {
    sealed class Event : ViewEvent {
        data class OnDismiss(val item: GroupDetailsWordItemUI) : Event()

        data class OnRecovered(val item: GroupDetailsWordItemUI) : Event()
        object OnRecover : Event()
        object OnApplyDismiss : Event()

        object OnBackClick : Event()

        object OnAddWordClick : Event()
        object OnRemoveGroupClick : Event()
        object ToFlashCards : Event()
        object ToAddSentence : Event()
    }

    data class State(
        val isLoading: Boolean = false, val deletedWords: List<WordWithGroups>
    ) : ViewState

    object ButtonId {
        const val flashcards: Int = -1000
        const val learn: Int = -1001
        const val share: Int = -1002
        const val remove: Int = -1003
        const val addWord = -1004
    }

    sealed class Effect : ViewSideEffect {
        data class GetWordsError(@StringRes val errorRes: Int) : Effect()
        object ShowRecover : Effect()

        sealed class Navigation : Effect() {
            object BackTo : Navigation()

            data class ToAddWordWithGroup(val group: GroupListItemsModel) : Navigation()
            data class ToFlashCards(val group: GroupListItemsModel) : Navigation()
            data class ToAddSentence(val group: GroupListItemsModel) : Navigation()
        }
    }
}
