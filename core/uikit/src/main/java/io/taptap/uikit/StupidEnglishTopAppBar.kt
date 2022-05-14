package io.taptap.uikit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.taptap.uikit.theme.StupidEnglishTheme

private val AppBarHeight = 56.dp

@Composable
fun StupidEnglishTopAppBar(
    text: String,
    modifier: Modifier = Modifier,
    onNavigationClick: (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color = backgroundColor)
            .fillMaxWidth()
            .height(AppBarHeight)
            .padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
    ) {
        if (onNavigationClick != null) {
            NavigationIcon(
                onClick = { onNavigationClick() }
            )
        }

        val textStartPadding = remember {
            if (onNavigationClick != null) {
                12.dp
            } else {
                0.dp
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            color = textColor,
            modifier = Modifier.padding(start = textStartPadding)
        )
    }
}

@Composable
private fun NavigationIcon(
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(onClick = { onClick() }) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "backIcon",
            tint = iconColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StupidEnglishTopAppBarPreview() {
    StupidEnglishTheme {
        StupidEnglishTopAppBar(
            text = "Test text",
            onNavigationClick = {}
        )
    }
}