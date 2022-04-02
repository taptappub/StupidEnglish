package io.taptap.stupidenglish.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.ui.theme.getStupidLanguageBackgroundBox

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
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    brush = getStupidLanguageBackgroundBox()
                )
        ) {
            content()
        }
    }
}
