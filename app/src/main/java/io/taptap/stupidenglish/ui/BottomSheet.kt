package io.taptap.stupidenglish.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    ConstraintLayout(modifier = modifier
        .navigationBarsPadding()
        .background(getStupidLanguageBackgroundBox())
    ) {
        val (stick, content) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.ic_rectangle),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 8.dp)
                .constrainAs(stick) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Box(modifier = Modifier
            .wrapContentSize()
            .constrainAs(content) {
                top.linkTo(stick.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {

            content()
        }
    }
}
