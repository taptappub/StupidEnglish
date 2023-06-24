package io.taptap.uikit.pager

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun PagerIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        userScrollEnabled = false,
    ) {
        items(totalDots) { index ->
            //draw selected & 2 neighbour dots
            if ((selectedIndex - index).absoluteValue > 2) {
                return@items
            }

            //FIXME анимации изменения цвета и размера не работают в превью
            val colorAnimation by animateColorAsState(
                targetValue = when (selectedIndex) {
                    index -> MaterialTheme.colors.primary
                    else -> Color.LightGray
                },
                label = "Pager indicator dot color"
            )
            val sizeAnimation by animateDpAsState(
                targetValue = when {
                    (selectedIndex - index).absoluteValue <= 1 -> 6.dp
                    else -> 3.dp
                },
                label = "Pager indicator dot size"
            )

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(colorAnimation)
                    .size(sizeAnimation)
            )
        }
    }
}