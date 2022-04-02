package io.taptap.uikit

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

val BOTTOM_BAR_VERTICAL_PADDING = 12.dp
val BOTTOM_BAR_HEIGHT = 56.dp
val BOTTOM_BAR_MARGIN = BOTTOM_BAR_HEIGHT + BOTTOM_BAR_VERTICAL_PADDING * 2

//ver2
@Composable
fun Fab(
    extended: Boolean,
    iconRes: Int,
    text: String,
    modifier: Modifier = Modifier,
    onFabClicked: () -> Unit
) {
    key(text) { // Prevent multiple invocations to execute during composition
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            onClick = onFabClicked,
            modifier = modifier
                .navigationBarsPadding()
                .padding(bottom = BOTTOM_BAR_MARGIN, start = 16.dp, end = 16.dp)
                .height(48.dp)
                .widthIn(min = 48.dp)
        ) {
            AnimatingFabContent(
                icon = {
                    Icon(
                        painterResource(id = iconRes),
                        contentDescription = text
                    )
                },
                text = {
                    Text(
                        style = MaterialTheme.typography.headlineLarge,
                        text = text
                    )
                },
                extended = extended
            )
        }
    }
}