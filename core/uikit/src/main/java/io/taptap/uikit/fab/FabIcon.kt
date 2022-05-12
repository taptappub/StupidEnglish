package io.taptap.uikit.fab

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
interface FabIcon {
    @Stable
    val iconRes: Int

    @Stable
    val iconRotate: Float?

    @Stable
    val description: String?
}

private class FabIconImpl(
    override val iconRes: Int,
    override val iconRotate: Float?,
    override val description: String?
) : FabIcon

/**
 * Affects the main fab icon.
 *
 * @param iconRes [MultiFloatingActionButton]'s main icon
 * @param iconRotate if is not null, the [iconRes] rotates as much as [iconRotate] when [MultiFloatingActionButton] is in [MultiFabState.Expand] state.
 */
fun FabIcon(
    @DrawableRes iconRes: Int,
    iconRotate: Float? = null,
    text: String? = null
): FabIcon =
    FabIconImpl(iconRes = iconRes, iconRotate = iconRotate, description = text)