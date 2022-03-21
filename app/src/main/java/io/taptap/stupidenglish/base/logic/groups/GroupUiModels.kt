package io.taptap.stupidenglish.base.logic.groups

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.ui.theme.DeepBlue

sealed class GroupListModels(
    open val id: Long,
    open val color: Color
)

data class GroupItemUI(
    override val id: Long,
    override val color: Color,
    val name: String
) : GroupListModels(id, color)

data class NoGroupItemUI(
    override val id: Long,
    override val color: Color,
    val titleRes: Int
) : GroupListModels(id, color)

val NoGroup = NoGroupItemUI(
    id = -1,
    titleRes = R.string.word_group_no_group_name,
    color = DeepBlue
)

@Composable
fun GroupListModels.getTitle():String {
    return when (this) {
        is GroupItemUI -> this.name
        is NoGroupItemUI -> stringResource(id = this.titleRes)
    }
}
