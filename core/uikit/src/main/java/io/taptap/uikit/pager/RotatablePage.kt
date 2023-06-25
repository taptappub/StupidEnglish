package io.taptap.uikit.pager

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.taptap.uikit.theme.StupidEnglishTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RotatablePage(
    word: Pair<String, String>,
    modifier: Modifier = Modifier,
) {
    var angle by remember {
        mutableFloatStateOf(0f)
    }

    //FIXME переворот карты не работает в превью
    val rotation = animateFloatAsState(
        targetValue = angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        ),
        label = "Page rotation"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationX = rotation.value
            },
        onClick = {
            angle = (angle + 180) % 360
        }
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    //rotate content back
                    rotationX = -rotation.value
                }
        ) {
            val (text, description) = word
            Text(
                text = if (rotation.value > 90f) description else text,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(all = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun RotatablePagePreview() {
    StupidEnglishTheme {
        RotatablePage(
            word = "Баклан" to "Человек, не разбирающийся в вопросе",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}