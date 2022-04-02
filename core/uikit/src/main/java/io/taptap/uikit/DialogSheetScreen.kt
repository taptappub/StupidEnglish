package io.taptap.uikit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun DialogSheetScreen(
    painter: Painter,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    okButtonText: String,
    cancelButtonText: String,
    onOkButtonClick: () -> Unit,
    onCancelButtonClick: () -> Unit
) {
    BottomSheetScreen(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.Center)
                )
            }

            LargeTitle(
                text = title,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            AverageText(
                text = message,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 44.dp)
            ) {
                SecondaryButton(
                    text = cancelButtonText,
                    modifier = Modifier.weight(1f),
                ) {
                    onCancelButtonClick()
                }
                Spacer(modifier = Modifier.width(8.dp))
                PrimaryButton(
                    text = okButtonText,
                    modifier = Modifier.weight(1f)
                ) {
                    onOkButtonClick()
                }
            }
        }
    }
}