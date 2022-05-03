package io.taptap.stupidenglish.features.importwords.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.logic.sources.groups.read.GroupListModels
import io.taptap.stupidenglish.features.words.ui.WordListContract

class ImportWordsContract {
    sealed class Event : ViewEvent {
        data class OnGroupSelect(val item: GroupListModels) : Event()
        object OnImportClick : Event()
        data class OnLinkChanging(val value: String) : Event()

        object OnAddGroupClick : Event()
        data class OnGroupChanging(val value: String) : Event()
        object OnApplyGroup : Event()
        object OnGroupAddingCancel : Event()
    }

    data class State(
        val link: String,
        val group: String,
        val isWrongLink: Boolean,
        val groups: List<GroupListModels>,
        val importWordState: ImportWordState,
        val selectedGroups: List<GroupListModels>,
        val parsingState: ParsingState
    ) : ViewState

    enum class ImportWordState {
        None,
        InProgress,
        HasLink
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
            object BackToWordList : Navigation()
        }
    }
}