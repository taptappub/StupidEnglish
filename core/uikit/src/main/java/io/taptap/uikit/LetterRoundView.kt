package io.taptap.uikit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.taptap.uikit.theme.StupidEnglishTheme

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
    LetterRoundView(
        selected = selected,
        color = color,
        border1 = border1,
        border2 = border2,
        modifier = modifier,
        elevation = elevation,
        shape = shape,
        content = {
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
    )
}

@Composable
fun LetterRoundView(
    selected: Boolean,
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
    elevation: Dp = 4.dp,
    shape: Shape = CircleShape,
    content: @Composable BoxScope.() -> Unit
) {
    /*Column(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)) {
        Box(
            modifier = Modifier.size(100.dp).clip(shape).background(Color.Red)
        )
    }*/
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(elevation = elevation, shape = shape, clip = false)
            .zIndex(elevation.value)
            .then(
                if (border2 != null) Modifier.border(border2, shape) else Modifier
            )
            .then(
                if (border1 != null) Modifier.border(border1, shape) else Modifier
            )
            .clip(shape)
            .background(
                color = color,
                shape = shape
            )
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun LetterRoundView() {
    StupidEnglishTheme {
        LetterRoundView(
            content = {
                Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary)
                )
            },
            selected = false,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(44.dp)
                .clickable {}
        )
    }
}
