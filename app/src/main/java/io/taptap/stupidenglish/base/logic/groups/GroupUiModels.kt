package io.taptap.stupidenglish.base.logic.groups

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.taptap.stupidenglish.R

sealed class GroupListModels(
    open val id: Long
)

data class GroupItemUI(
    override val id: Long,
    val name: String
) : GroupListModels(id)

data class NoGroupItemUI(
    override val id: Long,
    val titleRes: Int
) : GroupListModels(id)

val NoGroup = NoGroupItemUI(
    id = -1,
    titleRes = R.string.word_group_no_group_name
)

@Composable
fun GroupListModels.getTitle():String {
    return when (this) {
        is GroupItemUI -> this.name
        is NoGroupItemUI -> stringResource(id = this.titleRes)
    }
}
