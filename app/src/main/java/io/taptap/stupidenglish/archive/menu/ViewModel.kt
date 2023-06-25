package io.taptap.stupidenglish.archive.menu

import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.features.words.ui.WordListContract
import io.taptap.stupidenglish.ui.MenuItem

suspend fun handleEvents(event: WordListContract.Event) {
//    is WordListContract.Event.OnGroupMenuItemClick -> {
//        setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
//        handleMenuItem(event.item)
//        setEffect { WordListContract.Effect.HideBottomSheet }
//        setState {
//            copy(
//                sheetContentType = WordListContract.SheetContentType.Motivation,
//                longClickedGroup = NoGroup
//            )
//        }
//    }
//
//    is WordListContract.Event.OnGroupMenuCancel -> {
//        setEffect { WordListContract.Effect.ChangeBottomBarVisibility(isShown = true) }
//        setEffect { WordListContract.Effect.HideBottomSheet }
//        setNewGroupName("")
//        setState {
//            copy(
//                sheetContentType = WordListContract.SheetContentType.Motivation,
//                longClickedGroup = NoGroup
//            )
//        }
//    }
}

//private suspend fun handleMenuItem(item: MenuItem) {
//    val currentGroup = requireNotNull(viewState.value.longClickedGroup)
//    when (item.id) {
//        0 -> setEffect { WordListContract.Effect.Navigation.ToGroupDetails(group = currentGroup) }
//        1 -> setEffect { WordListContract.Effect.Navigation.ToAddWordWithGroup(group = currentGroup) }
//        2 -> setEffect { WordListContract.Effect.Navigation.ToFlashCards(currentGroup) }
//        3 -> setEffect { WordListContract.Effect.Navigation.ToAddSentence(currentGroup) }
//        4 -> error("Собирай статистику =)")
//        5 -> removeGroups(listOf(currentGroup))
//        else -> error("there is no such menu element")
//    }
//}

fun getGroupMenu(menuType: MenuType): List<MenuItem> {
    return when (menuType) {
        MenuType.Enabled -> listOf(
            MenuItem(0, R.string.word_menu_open),
            MenuItem(1, R.string.word_menu_add_word),
            MenuItem(2, R.string.word_menu_flashcards, true),
            MenuItem(3, R.string.word_menu_learn, true),
            MenuItem(4, R.string.word_menu_share, false),
            MenuItem(5, R.string.word_menu_remove)
        )

        MenuType.Disabled -> listOf(
            MenuItem(0, R.string.word_menu_open),
            MenuItem(1, R.string.word_menu_add_word),
            MenuItem(2, R.string.word_menu_flashcards, false),
            MenuItem(3, R.string.word_menu_learn, false),
            MenuItem(4, R.string.word_menu_share, false),
            MenuItem(5, R.string.word_menu_remove)
        )

        MenuType.AllWords -> listOf(
            MenuItem(0, R.string.word_menu_open),
            MenuItem(1, R.string.word_menu_add_word),
            MenuItem(2, R.string.word_menu_flashcards),
            MenuItem(3, R.string.word_menu_learn),
            MenuItem(4, R.string.word_menu_share, false),
            MenuItem(5, R.string.word_menu_remove, false)
        )
    }
}

//private fun handleMenuCreation(group: GroupListItemsModel) {
//    viewModelScope.launch(Dispatchers.IO) {
//        if (group == NoGroup) {
//            setState {
//                copy(
//                    sheetContentType = WordListContract.SheetContentType.GroupMenu,
//                    longClickedGroup = group,
//                    groupMenuList = getGroupMenu(WordListContract.MenuType.AllWords)
//                )
//            }
//        } else {
//            repository.getWordsCountInGroup(group.id)
//                .handle(
//                    success = { count ->
//                        if (count == 0) {
//                            setState {
//                                copy(
//                                    sheetContentType = WordListContract.SheetContentType.GroupMenu,
//                                    longClickedGroup = group,
//                                    groupMenuList = getGroupMenu(WordListContract.MenuType.Disabled)
//                                )
//                            }
//                        } else {
//                            setState {
//                                copy(
//                                    sheetContentType = WordListContract.SheetContentType.GroupMenu,
//                                    longClickedGroup = group,
//                                    groupMenuList = getGroupMenu(WordListContract.MenuType.Enabled)
//                                )
//                            }
//                        }
//                    },
//                    error = {
//                        setEffect { WordListContract.Effect.GetWordsError(R.string.word_get_list_error) }
//                    }
//                )
//        }
//    }
//}