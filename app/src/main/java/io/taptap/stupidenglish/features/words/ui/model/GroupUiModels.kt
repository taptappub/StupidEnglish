package io.taptap.stupidenglish.features.words.ui.model

import androidx.compose.ui.graphics.Color

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
