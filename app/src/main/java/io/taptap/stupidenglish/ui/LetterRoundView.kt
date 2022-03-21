package io.taptap.stupidenglish.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.taptap.stupidenglish.ui.theme.White100

@Composable
fun LetterRoundView(
    letter: Char,
    color: Color,
    modifier: Modifier,
    fontSize: TextUnit,
    border1: BorderStroke? = null,
    border2: BorderStroke? = null,
    elevation: Dp = 0.dp,
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
            fontWeight = FontWeight.Bold,
            color = White100,
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}