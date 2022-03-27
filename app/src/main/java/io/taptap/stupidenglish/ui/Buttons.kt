package io.taptap.stupidenglish.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    color: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    text: String,
    onClick: () -> Unit
) {
    Button(
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