package io.taptap.stupidenglish.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import io.taptap.stupidenglish.ui.theme.getFABColor
import io.taptap.stupidenglish.ui.theme.getFABTextColor

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
            containerColor = getFABColor(),
            contentColor = getFABTextColor(),
            onClick = onFabClicked,
            modifier = modifier
                .padding(16.dp)
                .navigationBarsPadding()
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
                        text = text
                    )
                },
                extended = extended
            )
        }
    }
}