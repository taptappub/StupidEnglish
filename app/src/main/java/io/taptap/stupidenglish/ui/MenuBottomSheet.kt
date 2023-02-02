package io.taptap.stupidenglish.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.taptap.stupidenglish.R
import io.taptap.uikit.AverageTitle
import io.taptap.uikit.BottomSheetScreen
import io.taptap.uikit.Divider
import io.taptap.uikit.LargeTitle
import io.taptap.uikit.theme.StupidEnglishTheme

data class MenuItem(
    val id: Int,
    @StringRes val stringId: Int,
    val enabled: Boolean = true
)

@Composable
fun MenuBottomSheet(
    list: List<MenuItem>,
    onClick: (MenuItem) -> Unit,
    @StringRes titleRes: Int,
    modifier: Modifier
) {
    BottomSheetScreen(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AverageTitle(
                text = stringResource(id = titleRes),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            )

            MenuContent(
                list = list,
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
fun MenuContent(
    list: List<MenuItem>,
    onClick: (MenuItem) -> Unit,
    modifier: Modifier
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
    ) {
        itemsIndexed(items = list) { index, item ->
            MenuItemRow(
                item = item,
                onClick = onClick
            )
            if (index < list.lastIndex)
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
        }
    }
}

@Composable
private fun MenuItemRow(
    item: MenuItem,
    onClick: (MenuItem) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .then(
                if (item.enabled) {
                    Modifier.clickable { onClick(item) }
                } else {
                    Modifier
                }
            )
    ) {
        LargeTitle(
            isEnabled = item.enabled,
            text = stringResource(id = item.stringId),
            modifier = Modifier
                .weight(1.0f, true)
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MenuBottomSheet() {
    StupidEnglishTheme {
        MenuBottomSheet(
            modifier = Modifier,
            onClick = {},
            titleRes = R.string.adds_motivation_title,
            list = listOf(
//                MenuItem(0, "Remove"),
//                MenuItem(1, "Learn"),
//                MenuItem(2, "Shoot"),
//                MenuItem(3, "Fun"),
//                MenuItem(4, "Long long long long text")
            )
        )
    }
}