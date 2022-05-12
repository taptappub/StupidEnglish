package io.taptap.uikit.fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.taptap.uikit.R

@Composable
fun NextButton(
    visibility: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    key("NextButton") {
        Box(
            modifier = modifier
                .size(52.dp)
        ) {
            AnimatedVisibility(
                visible = visibility,
                enter = fadeIn(
                    initialAlpha = 0.3f
                ),
                exit = fadeOut()
            ) {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                    onClick = onClick,
                    modifier = Modifier
                        .size(52.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "next",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                    )
                }
            }
        }
    }
}