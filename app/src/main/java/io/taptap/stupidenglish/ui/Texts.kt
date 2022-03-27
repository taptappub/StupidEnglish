package io.taptap.stupidenglish.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.taptap.stupidenglish.R

@Composable
fun LargeTitle(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.labelLarge
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = color,
        style = style,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun AverageTitle(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.headlineLarge
) {
    Text(
        text = text,
        textAlign = TextAlign.Left,
        color = color,
        style = style,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun AverageText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.titleSmall
) {
    Text(
        text = text,
        textAlign = textAlign,
        color = color,
        style = style,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}