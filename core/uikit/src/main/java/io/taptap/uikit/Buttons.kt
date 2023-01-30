package io.taptap.uikit

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.taptap.uikit.theme.StupidEnglishTheme

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    text: String,
    startImagePainter: Painter? = null,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    onClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(44.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        onClick = onClick
    ) {
        if (startImagePainter != null) {
            Image(
                painter = startImagePainter,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
            )
        }
        Text(
            text = text,
            color = textColor,
            style = textStyle
        )
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    style: TextStyle = MaterialTheme.typography.titleSmall,
    text: String,
    onClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(44.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        onClick = onClick
    ) {
        Text(
            text = text,
            color = textColor,
            style = style
        )
    }
}

@Preview()
@Composable
fun PrimaryButton() {
    StupidEnglishTheme {
        PrimaryButton(
            text = "Ass",
            onClick = {}
        )
    }
}

@Preview()
@Composable
fun SecondaryButton() {
    StupidEnglishTheme {
        SecondaryButton(
            text = "Ass2",
            onClick = {}
        )
    }
}
