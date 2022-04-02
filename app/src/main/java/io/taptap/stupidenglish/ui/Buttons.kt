package io.taptap.stupidenglish.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
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
            style = MaterialTheme.typography.titleSmall
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