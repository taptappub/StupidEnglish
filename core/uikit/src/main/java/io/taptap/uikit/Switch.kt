package io.taptap.uikit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedTrackColor = Color.Transparent,
        uncheckedTrackColor = Color.Transparent,

        checkedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
        uncheckedBorderColor = MaterialTheme.colorScheme.surfaceVariant,

        checkedThumbColor = MaterialTheme.colorScheme.tertiary,
        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
    ),
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = colors,
        modifier = modifier
    )
}