package io.taptap.stupidenglish.features.words.ui

import androidx.annotation.StringRes
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.model.WordWithGroups
import io.taptap.stupidenglish.features.words.ui.model.WordListItemUI
import io.taptap.stupidenglish.features.words.ui.model.WordListListModels
import io.taptap.stupidenglish.ui.MenuItem
import io.taptap.uikit.group.GroupListItemsModel

class WordListContract {
    sealed class Event : ViewEvent {
        data class OnWordDismiss(val item: WordListItemUI) : Event()

        data class OnRecovered(val item: WordListListModels) : Event()
        object OnRecover : Event()
        object OnApplyDismiss : Event()

        object OnEditGroupClick : Event()
        object OnOnboardingClick : Event()
        object OnWordClick : Event()
        object OnProfileClick : Event()

        object OnMotivationConfirmClick : Event()
        object OnMotivationDeclineClick : Event()
        object OnMotivationCancel : Event()

        object OnAddGroupClick : Event()
        object OnApplyGroup : Event()
        object OnGroupAddingCancel : Event()

        object OnViewAllClick : Event()
        data class OnGroupLongClick(val group: GroupListItemsModel) : Event()
        data class OnGroupClick(val group: GroupListItemsModel) : Event()
        data class OnGroupMove(val fromItemId: Long, val toItemId: Long) : Event()
    }

    data class State(
        val isLoading: Boolean = false,
        val sheetContentType: SheetContentType,
        val deletedWords: MutableList<WordWithGroups>,
        val avatar: String?,
        val longClickedGroup: GroupListItemsModel
    ) : ViewState

    enum class SheetContentType {
        AddGroup,
        Motivation
    }

    sealed class Effect : ViewSideEffect {
        data class GetWordsError(@StringRes val errorRes: Int) : Effect()
        data class GetUserError(@StringRes val errorRes: Int) : Effect()

        object HideBottomSheet : Effect()
        object ShowBottomSheet : Effect()
        object ShowUnderConstruction : Effect()
        object ShowRecover : Effect()
        data class ChangeBottomBarVisibility(val isShown: Boolean) : Effect()

        sealed class Navigation : Effect() {
            object ToProfile : Navigation()
            object ToGroupList : Navigation()

            data class ToGroupDetails(val group: GroupListItemsModel) : Navigation()
        }
    }
}
