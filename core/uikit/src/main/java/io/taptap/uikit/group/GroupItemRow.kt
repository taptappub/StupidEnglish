package io.taptap.uikit.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.uikit.LetterRoundView
import io.taptap.uikit.theme.StupidEnglishTheme

@Composable
fun GroupItemRow(
    title: String,
    button: String,
    currentGroup: GroupListModels?,
    list: List<GroupListModels>,
    onButtonClicked: () -> Unit,
    onGroupClicked: (GroupListModels) -> Unit,
    onGroupLongClicked: (GroupListModels) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp)
    ) {
        GroupItemHeader(
            title = title,
            button = button,
            onButtonClicked = onButtonClicked
        )
        GroupItemGroupsRow(
            list = list,
            currentGroup = currentGroup,
            onGroupClicked = onGroupClicked,
            onGroupLongClicked = onGroupLongClicked
        )
    }
}

@Composable
private fun GroupItemGroupsRow(
    list: List<GroupListModels>,
    currentGroup: GroupListModels?,
    onGroupClicked: (GroupListModels) -> Unit,
    onGroupLongClicked: (GroupListModels) -> Unit
) {
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        items(
            items = list,
            key = { it.id }
        ) { item ->
            when (item) {
                is NoGroupItemUI -> GroupItem(
                    title = stringResource(id = item.titleRes),
                    group = item,
                    selected = currentGroup == item,
                    onGroupClicked = onGroupClicked,
                    onGroupLongClicked = onGroupLongClicked
                )
                is GroupItemUI -> GroupItem(
                    title = item.name,
                    group = item,
                    selected = currentGroup == item,
                    onGroupClicked = onGroupClicked,
                    onGroupLongClicked = onGroupLongClicked
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GroupItem(
    title: String,
    group: GroupListModels,
    selected: Boolean,
    onGroupClicked: (GroupListModels) -> Unit,
    onGroupLongClicked: (GroupListModels) -> Unit
) {
    Column(
        modifier = Modifier
            .width(60.dp)
            .padding(horizontal = 2.dp)
    ) {
        LetterRoundView(
            letter = title[0].uppercaseChar(),
            selected = selected,
            fontSize = 28.sp,
            modifier = Modifier
                .size(56.dp)
                .then(
                    if (group == NoGroup) {
                        Modifier.clickable { onGroupClicked(group) }
                    } else {
                        Modifier.combinedClickable(
                            onClick = { onGroupClicked(group) },
                            onLongClick = { onGroupLongClicked(group) },
                        )
                    }
                )
        )
        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.secondary
            },
            style = if (selected) {
                MaterialTheme.typography.bodyMedium
            } else {
                MaterialTheme.typography.bodySmall
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupItemRow() {
    StupidEnglishTheme {
        GroupItemRow(
            title = "Groups",
            button = "Add",
            currentGroup = null,
            onButtonClicked = {},
            list = emptyList(),
            onGroupClicked = {},
            onGroupLongClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupItem() {
    StupidEnglishTheme {
        GroupItem(
            title = "Title",
            group = NoGroupItemUI(
                id = -1,
                titleRes = -1
            ),
            selected = true,
            onGroupClicked = {},
            onGroupLongClicked = {}
        )
    }
}
