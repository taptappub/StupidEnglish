package io.taptap.stupidenglish.ui.bottomsheet

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.logic.groups.GroupItemUI
import io.taptap.stupidenglish.base.logic.groups.GroupListModels
import io.taptap.stupidenglish.base.logic.groups.NoGroup
import io.taptap.stupidenglish.base.logic.groups.getTitle
import io.taptap.stupidenglish.ui.BottomSheetScreen
import io.taptap.stupidenglish.ui.LetterRoundView
import io.taptap.stupidenglish.ui.theme.Grey200
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import io.taptap.stupidenglish.ui.theme.White100
import io.taptap.stupidenglish.ui.theme.getContentTextColor
import io.taptap.stupidenglish.ui.theme.getTitleTextColor

@Composable
fun ChooseGroupBottomSheetScreen(
    list: List<GroupListModels>,
    selectedList: List<GroupListModels>,
    @StringRes titleRes: Int,
    @StringRes buttonRes: Int,
    modifier: Modifier,
    onItemClick: (GroupListModels) -> Unit,
    onButtonClick: () -> Unit
) {
    BottomSheetScreen(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = titleRes),
                textAlign = TextAlign.Left,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = getTitleTextColor(),
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            ChooseGroupContent(
                list = list,
                selectedList = selectedList,
                onItemClick = onItemClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )

            Button(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 36.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary),
                onClick = onButtonClick
            ) {
                Text(
                    text = stringResource(id = buttonRes),
                    color = White100
                )
            }
        }
    }
}

@Composable
private fun ChooseGroupContent(
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
            if (index < list.lastIndex) Divider(color = Grey200)
        }
    }
}

@Composable
fun GroupItemRow(
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
            elevation = 8.dp,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .size(28.dp)
        )
        Text(
            text = item.getTitle(),
            textAlign = TextAlign.Left,
            fontSize = 15.sp,
            color = getContentTextColor(),
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1.0f, true)
                .fillMaxWidth()
                .padding(2.dp)
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
fun ChooseGroupBottomSheetScreenPreview() {
    StupidEnglishTheme {
        ChooseGroupBottomSheetScreen(
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
            selectedList = listOf(NoGroup),
            onItemClick = {},
            onButtonClick = {},
            buttonRes = R.string.addw_group_button,
            titleRes = R.string.addw_group_choose_group_title,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
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