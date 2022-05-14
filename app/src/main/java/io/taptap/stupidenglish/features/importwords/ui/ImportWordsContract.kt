package io.taptap.stupidenglish.features.importwords.ui

import androidx.annotation.StringRes
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.logic.sources.groups.read.GroupListModels

class ImportWordsContract {
    sealed class Event : ViewEvent {
        data class OnGroupSelect(val item: GroupListModels) : Event()
        object OnImportClick : Event()
        data class OnLinkChanging(val value: String) : Event()

        object OnAddGroupClick : Event()
        data class OnGroupChanging(val value: String) : Event()
        object OnApplyGroup : Event()
        object OnGroupAddingCancel : Event()

        object OnTutorialClick : Event()
        object OnBackClick : Event()
    }

    data class State(
        val link: String,
        val group: String,
        val groups: List<GroupListModels>,
        val isAddGroup: Boolean,
        val importWordState: ImportWordState,
        val selectedGroups: List<GroupListModels>,
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