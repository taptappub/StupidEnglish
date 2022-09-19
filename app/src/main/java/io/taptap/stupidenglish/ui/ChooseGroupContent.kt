package io.taptap.stupidenglish.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.stupidenglish.R
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListModels
import io.taptap.uikit.group.NoGroup
import io.taptap.uikit.group.getTitle
import io.taptap.uikit.AverageText
import io.taptap.uikit.Divider
import io.taptap.uikit.LetterRoundView
import io.taptap.uikit.theme.StupidEnglishTheme

@Composable
fun ChooseGroupContent(
    list: List<GroupListModels>,
    selectedList: List<GroupListModels>,
    modifier: Modifier,
    onItemClick: (GroupListModels) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
    ) {
        itemsIndexed(items = list) { index, item ->
            GroupItemRow(
                item = item,
                isSelected = selectedList.contains(item)
            ) {
                onItemClick(item)
            }
            if (index < list.lastIndex)
                Divider(modifier = Modifier
                    .padding(horizontal = 20.dp))
        }
    }
}

@Composable
private fun GroupItemRow(
    item: GroupListModels,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (item != NoGroup) {
                    Modifier.clickable { onItemClick() }
                } else {
                    Modifier
                }
            )
    ) {
        LetterRoundView(
            letter = item.getTitle()[0].uppercaseChar(),
            selected = isSelected,
            elevation = 4.dp,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .size(28.dp)
        )
        AverageText(
            text = item.getTitle(),
            maxLines = 1,
            modifier = Modifier
                .weight(1.0f, true)
                .fillMaxWidth()
                .padding(2.dp),
            textAlign = TextAlign.Left,
        )

        Image(
            painter = painterResource(
                id = if (isSelected) {
                    R.drawable.ic_check_circle
                } else {
                    R.drawable.ic_uncheck_circle
                }
            ),
            contentDescription = "check",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupItemPreview() {
    StupidEnglishTheme {
        GroupItemRow(
            GroupItemUI(
                id = 1,
                name = "test name"
            ),
            true
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupsPreview() {
    StupidEnglishTheme {
        ChooseGroupContent(
            list = listOf(
                GroupItemUI(
                    id = 1,
                    name = "test name"
                ),
                GroupItemUI(
                    id = 1,
                    name = "test name"
                ),
                GroupItemUI(
                    id = 1,
                    name = "test name"
                )
            ),
            selectedList = listOf(
                GroupItemUI(
                    id = 1,
                    name = "test name"
                ),
                GroupItemUI(
                    id = 1,
                    name = "test name"
                )
            ),
            modifier = Modifier
                .fillMaxWidth(),
            onItemClick = {}
        )
    }
}
