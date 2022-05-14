package io.taptap.uikit

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StupidEnglishTopAppBar(
    text: String,
    modifier: Modifier = Modifier,
    onNavigationClick: (() -> Unit)? = null,
    elevation: Dp = 12.dp,
    backgroundColor: Color = Color.Transparent,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    TopAppBar(
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                color = textColor
            )
        },
        navigationIcon = if (onNavigationClick != null) NavigationIcon(onNavigationClick) else null,
        backgroundColor = backgroundColor,
        contentColor = Color.Yellow,
        elevation = elevation,
        modifier = modifier.systemBarsPadding()
    )
}

@Composable
private fun NavigationIcon(
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.onSurface
) = @Composable {
    IconButton(onClick = { onClick() }) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "backIcon",
            tint = iconColor
        )
    }
}
