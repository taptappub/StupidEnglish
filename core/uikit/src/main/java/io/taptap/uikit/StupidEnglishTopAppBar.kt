package io.taptap.uikit

import android.media.Image
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.taptap.uikit.theme.StupidEnglishTheme

private val AppBarHeight = 56.dp

@Composable
fun StupidEnglishTopAppBar(
    text: String,
    modifier: Modifier = Modifier,
    onNavigationClick: (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    vararg actions: @Composable RowScope.() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color = backgroundColor)
            .padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .height(AppBarHeight)
    ) {
        if (onNavigationClick != null) {
            NavigationIcon(
                onClick = { onNavigationClick() }
            )
        }

        val startPadding = remember {
            if (onNavigationClick != null) {
                12.dp
            } else {
                16.dp
            }
        }

        val endPadding = remember {
            if (actions.isEmpty()) {
                16.dp
            } else {
                0.dp
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            color = textColor,
            modifier = Modifier.padding(start = startPadding, end = endPadding)
        )

        Spacer(modifier = Modifier.weight(1f))

        actions.forEach {
            it()
        }
    }
}

@Composable
private fun NavigationIcon(
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        onClick = { onClick() },
    ) {
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