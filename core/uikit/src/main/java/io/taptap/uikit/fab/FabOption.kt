package io.taptap.uikit.fab

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
interface FabOption {
    @Stable
    val contentColor: Color

    @Stable
    val containerColor: Color

    @Stable
    val showLabels: Boolean
}

private class FabOptionImpl(
    override val contentColor: Color,
    override val containerColor: Color,
    override val showLabels: Boolean
) : FabOption

/**
 * Affects all fabs including sub fabs.
 */
@SuppressLint("ComposableNaming")
@Composable
fun FabOption(
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    showLabels: Boolean = false
): FabOption =
    FabOptionImpl(contentColor = contentColor, containerColor = containerColor, showLabels = showLabels)