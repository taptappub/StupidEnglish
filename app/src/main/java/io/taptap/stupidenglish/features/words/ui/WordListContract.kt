package io.taptap.stupidenglish.features.words.ui

import androidx.annotation.StringRes
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.features.words.ui.model.WordListItemUI
import io.taptap.stupidenglish.features.words.ui.model.WordListListModels
import io.taptap.stupidenglish.ui.MenuItem
import io.taptap.uikit.group.GroupListItemsModels
import io.taptap.uikit.group.GroupListModels

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

        object OnGroupMenuCancel : Event()
        data class OnGroupMenuItemClick(val item: MenuItem) : Event()
        data class OnGroupClick(val group: GroupListItemsModels) : Event()
        data class OnGroupLongClick(val group: GroupListItemsModels) : Event()
    }

    data class State(
        val wordList: List<WordListListModels>,
        val isLoading: Boolean = false,
        val currentGroup: GroupListItemsModels,
        val sheetContentType: SheetContentType,
        val deletedWordIds: MutableList<Long>,
        val avatar: String?,
        val longClickedGroup: GroupListItemsModels,
        val groupMenuList: List<MenuItem>
    ) : ViewState

    enum class SheetContentType {
        AddGroup,
        GroupMenu,
        Motivation
    }

    enum class MenuType {
        Enabled,
        Disabled,
        AllWords
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
            object ToAddWord : Navigation()
            object ToImportWords : Navigation()
            object ToProfile : Navigation()
//            data class ToAddSentence(val wordIds: List<Long>) : Navigation()

            data class ToGroupDetails(val group: GroupListItemsModels) : Navigation()
            data class ToAddWordWithGroup(val group: GroupListItemsModels) : Navigation()
            data class ToFlashCards(val group: GroupListItemsModels) : Navigation()
            data class ToAddSentence(val group: GroupListItemsModels) : Navigation()
        }

        /*MenuItem(0, R.string.word_menu_open),
        MenuItem(1, R.string.word_menu_add_word),
        MenuItem(2, R.string.word_menu_flashcards),
        MenuItem(3, R.string.word_menu_learn),
        MenuItem(4, R.string.word_menu_remove)*/
    }
}
