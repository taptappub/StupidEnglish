package io.taptap.stupidenglish.archive.menu

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.ui.MenuItem

sealed class Event : ViewEvent {
    object OnGroupMenuCancel : Event()
    data class OnGroupMenuItemClick(val item: MenuItem) : Event()
}

data class State(
    val groupMenuList: List<MenuItem>
) : ViewState

enum class SheetContentType {
    GroupMenu
}

enum class MenuType {
    Enabled,
    Disabled,
    AllWords
}