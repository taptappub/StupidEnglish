package io.taptap.stupidenglish.features.details.ui


import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import kotlin.math.min


@Composable
fun StupidWordScreen(state: StupidWordContract.State) {
    val scrollState = rememberLazyListState()
    val scrollOffset: Float = min(
        1f,
        1 - (scrollState.firstVisibleItemScrollOffset / 600f + scrollState.firstVisibleItemIndex)
    )
    Surface(color = MaterialTheme.colors.background) {
        /*Column {
            Surface(elevation = 4.dp) {
                CategoryDetailsCollapsingToolbar(state.category!!, scrollOffset)
            }
            Spacer(modifier = Modifier.height(2.dp))
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.categoryWordItems) { item ->
                    WordItemRow(
                        item = item
                    )
                }
            }
        }*/
    }
}
/*

@Composable
private fun CategoryDetailsCollapsingToolbar(
    category: WordItem,
    scrollOffset: Float,
) {
    val imageSize by animateDpAsState(targetValue = max(72.dp, 128.dp * scrollOffset))
    Row {
        WordItem(
            item = category,
            modifier = Modifier
                .padding(
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth()
        )
    }
}*/
