package io.taptap.uikit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LoadingBar(
    contentAlignment: Alignment = Alignment.Center,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        contentAlignment = contentAlignment,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(color = color)
    }
}