package io.taptap.stupidenglish.features.words.ui

import androidx.annotation.StringRes
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.uikit.group.GroupListModels
import io.taptap.stupidenglish.features.words.ui.model.WordListItemUI
import io.taptap.stupidenglish.features.words.ui.model.WordListListModels

class WordListContract {
    sealed class Event : ViewEvent {
        data class OnWordDismiss(val item: WordListItemUI) : Event()

        data class OnRecovered(val item: WordListListModels) : Event()
        object OnRecover : Event()
        object OnApplySentenceDismiss : Event()

        object OnAddWordClick : Event()
        object OnImportWordsClick : Event()
        object OnOnboardingClick : Event()
        object OnWordClick : Event()
        object OnProfileClick : Event()

        object OnMotivationConfirmClick : Event()
        object OnMotivationDeclineClick : Event()
        object OnMotivationCancel : Event()

        object OnAddGroupClick : Event()
        object OnApplyGroup : Event()
        object OnGroupAddingCancel : Event()
        object OnApplyGroupsRemove : Event()

        object OnGroupRemovingCancel : Event()
        data class OnGroupSelect(val item: GroupListModels) : Event()
        data class OnGroupChanging(val value: String) : Event()
        data class OnGroupClick(val group: GroupListModels) : Event()
        data class OnGroupLongClick(val group: GroupListModels) : Event()
    }

    data class State(
        val wordList: List<WordListListModels>,
        val removedGroups: List<GroupListModels>,
        val dialogGroups: List<GroupListModels>,
        val isLoading: Boolean = false,
        val currentGroup: GroupListModels,
        val sheetContentType: SheetContentType,
        val group: String,
        val deletedWordIds: MutableList<Long>,
        val avatar: String?
    ) : ViewState

    enum class SheetContentType {
        AddGroup,
        RemoveGroup,
        Motivation
    }

    sealed class Effect : ViewSideEffect {
        data class GetRandomWordsError(@StringRes val errorRes: Int) : Effect()
        data class GetWordsError(@StringRes val errorRes: Int) : Effect()
        data class GetUserError(@StringRes val errorRes: Int) : Effect()

        object HideBottomSheet : Effect()
        object ShowBottomSheet : Effect()
        object ShowUnderConstruction : Effect()
        object ShowRecover : Effect()
        data class ChangeBottomBarVisibility(val isShown: Boolean) : Effect()

        sealed class Navigation : Effect() {
            object ToAddWord : Navigation()
            object ToImportWords : Navigation()
            object ToProfile : Navigation()
//            data class ToAddSentence(val wordIds: List<Long>) : Navigation()
        }
    }
}
