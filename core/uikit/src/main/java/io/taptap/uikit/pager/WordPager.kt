package io.taptap.uikit.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.taptap.uikit.theme.StupidEnglishTheme
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordPager(
    words: List<Pair<String, String>>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val pagerHeight = 220.dp
    val pageHeight = 200.dp

    val singlePageWithNeighbourEdges = object : PageSize {
        private val edgesWidth = 24.dp

        override fun Density.calculateMainAxisPageSize(availableSpace: Int, pageSpacing: Int): Int =
            availableSpace - edgesWidth.roundToPx()
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .height(pagerHeight),
        pageSize = singlePageWithNeighbourEdges,
        pageSpacing = 8.dp,
        contentPadding = PaddingValues(
            start = 30.dp, //FIXME MAGIC NUMBERS. Подбирал. Почему именно такие?
            end = 8.dp
        ),
    ) { page ->
        val pageHeightDelta = calculatePageHeightDelta(pagerState, page)

        RotatablePage(
            word = words[page],
            modifier = Modifier
                .fillMaxWidth()
                .height(pageHeight + pageHeightDelta)
        )
    }
}

@ExperimentalFoundationApi
private fun calculatePageHeightDelta(
    pagerState: PagerState,
    page: Int
): Dp = if (page != pagerState.currentPage) {
    0.dp
} else {
    val maxDeltaValue = 10.dp
    val pageOffset = pagerState.currentPageOffsetFraction.absoluteValue
    maxDeltaValue * (1f - pageOffset * 2)
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun WordPagerPreview() {
    StupidEnglishTheme {
        val words = listOf(
            "Баклан" to "Человек, не разбирающийся в вопросе",
            "Ёкать" to "Издавать от неожиданности неопределенные отрывистые звуки",
            "Ёрничать" to "Озорничать, допускать колкости по отношению к другим",
            "Изюбрь" to "Грациозный благородный олень",
        )

        Column {
            val pagerState = rememberPagerState {
                words.size
            }

            WordPager(
                pagerState = pagerState,
                words = words,
            )
            PagerIndicator(
                totalDots = pagerState.pageCount,
                selectedIndex = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
