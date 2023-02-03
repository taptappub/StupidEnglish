package io.taptap.stupidenglish.features.groupdetails.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.features.words.ui.model.WordListItemUI
import io.taptap.stupidenglish.features.words.ui.model.WordListListModels
import io.taptap.uikit.group.GroupListItemsModel

class GroupDetailsContract {
    sealed class Event : ViewEvent {
        data class OnWordDismiss(val item: WordListItemUI) : Event()

        data class OnRecovered(val item: WordListListModels) : Event()
        object OnRecover : Event()
        object OnApplyDismiss : Event()

        object OnBackClick: Event()

        data class OnShareClick(val group: GroupListItemsModel) : Event()
    }

    data class State(
        val wordList: List<WordListListModels>,
        val isLoading: Boolean = false,
        val deletedWordIds: MutableList<Long>
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class GetGroupsError(val errorRes: Int) : Effect()
        object ShowRecover : Effect()

        sealed class Navigation : Effect() {
            object BackTo : Navigation()

            data class ToAddWordWithGroup(val group: GroupListItemsModel) : Effect.Navigation()
            data class ToFlashCards(val group: GroupListItemsModel) : Effect.Navigation()
            data class ToAddSentence(val group: GroupListItemsModel) : Effect.Navigation()
        }
    }
}