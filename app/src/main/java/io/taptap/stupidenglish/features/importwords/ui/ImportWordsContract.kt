package io.taptap.stupidenglish.features.importwords.ui

import androidx.annotation.StringRes
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.uikit.group.GroupListItemsModel

class ImportWordsContract {
    sealed class Event : ViewEvent {
        data class OnGroupSelect(val item: GroupListItemsModel) : Event()
        object OnImportClick : Event()

        object OnAddGroupClick : Event()
        object OnApplyGroup : Event()
        object OnGroupAddingCancel : Event()

        object OnTutorialClick : Event()
        object OnBackClick : Event()
    }

    data class State(
        val groups: List<GroupListItemsModel>,
        val isAddGroup: Boolean,
        val importWordState: ImportWordState,
        val selectedGroups: List<GroupListItemsModel>,
        val parsingState: ParsingState
    ) : ViewState

    sealed class ImportWordState {
        object None: ImportWordState()
        object InProgress: ImportWordState()
        object HasLink: ImportWordState()
        data class Error(@StringRes val messageId: Int): ImportWordState()
    }

    enum class ParsingState {
        Success,
        InProgress,
        Failed,
        None
    }

    sealed class Effect : ViewSideEffect {
        object HideBottomSheet : Effect()
        object ShowBottomSheet : Effect()

        data class GetGroupsError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            object GoToImportTutorial : Navigation()
            object BackToWordList : Navigation()
        }
    }
}