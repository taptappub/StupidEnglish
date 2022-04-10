package io.taptap.uikit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.taptap.uikit.theme.getStupidLanguageBackgroundBox

@Composable
fun BottomSheetScreen(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .background(Color.Transparent)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_rectangle),
            contentDescription = null,
            modifier = Modifier.padding(bottom = 8.dp)
                .align(CenterHorizontally)
        )
        Box(
            Modifier
                .wrapContentSize()
                .background(
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                    brush = getStupidLanguageBackgroundBox()
                )
        ) {
            content()
        }
    }
}
