package io.taptap.uikit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun LetterRoundView(
    letter: Char,
    selected: Boolean,
    textColor: Color = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.secondary
    },
    color: Color = if (selected) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.surface
    },
    border1: BorderStroke? = if (selected) {
        BorderStroke(
            width = 4.dp,
            color = MaterialTheme.colorScheme.background
        )
    } else {
        null
    },
    border2: BorderStroke? = if (selected) {
        BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
    } else {
        null
    },
    modifier: Modifier,
    fontSize: TextUnit,
    elevation: Dp = 4.dp,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape, clip = false)
            .zIndex(elevation.value)
            .then(
                if (border2 != null) Modifier.border(border2, shape) else Modifier
            )
            .then(
                if (border1 != null) Modifier.border(border1, shape) else Modifier
            )
            .background(
                color = color,
                shape = shape
            )
            .clip(shape)
    ) {
        Text(
            text = letter.toString(),
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
