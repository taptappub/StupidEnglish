package io.taptap.stupidenglish.archive.ui

import androidx.annotation.PluralsRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.stupidenglish.R
import io.taptap.uikit.AverageTitle
import io.taptap.uikit.Divider
import io.taptap.uikit.LetterRoundView
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListItemsModels
import io.taptap.uikit.group.getTitle
import io.taptap.uikit.theme.StupidEnglishTheme

@Composable
fun GroupsStackRow(
    groups: List<GroupListItemsModels>,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier
        .height(52.dp)
        .clickable { onClick() }
    ) {
        Divider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when (groups.size) {
                0 -> error("Groups count can't be 0")
                1 -> OneGroup(groups.first())
                in 2..5 -> ManyGroups(
                    groups = groups,
                    text = getQuantityResource(R.plurals.addw_group_count, groups.size, groups.size)
                )
                else -> ManyGroups(
                    groups = groups.take(5),
                    text = getQuantityResource(R.plurals.addw_group_count, groups.size, groups.size)
                )
            }
            Image(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
private fun ManyGroups(groups: List<GroupListItemsModels>, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentWidth()
    ) {
        StackRowOfGroups(groups)

        AverageTitle(
            text = text,
            maxLines = 1,
            modifier = Modifier
                .padding(2.dp)
        )
    }
}

@Composable
private fun StackRowOfGroups(groups: List<GroupListItemsModels>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-14).dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 16.dp)
    ) {
        groups.forEach {
            LetterRoundView(
                letter = it.getTitle()[0].uppercaseChar(),
                selected = true,
                border1 = BorderStroke(
                    width = 2.dp,
                    color = Color.White
                ),
                border2 = null,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(28.dp)
            )
        }
    }
}

@Composable
private fun OneGroup(first: GroupListItemsModels) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentWidth()
    ) {
        LetterRoundView(
            letter = first.getTitle()[0].uppercaseChar(),
            selected = true,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .size(28.dp)
        )
        AverageTitle(
            text = first.getTitle(),
            maxLines = 1,
            modifier = Modifier
                .padding(2.dp)
        )
    }
}

@Composable
private fun getQuantityResource(
    @PluralsRes id: Int,
    quantity: Int,
    vararg formatArgs: Any
): String {
    val resources = LocalContext.current.resources
    return resources.getQuantityString(id, quantity, *formatArgs)
}

@Preview(showBackground = true)
@Composable
fun ManyGroupsPreview() {
    StupidEnglishTheme {
        val groups = listOf(
            GroupItemUI(
                id = 1,
                name = "test name"
            ),
            GroupItemUI(
                id = 1,
                name = "dtest name"
            ),
            GroupItemUI(
                id = 1,
                name = "dtest name"
            )
        )
        ManyGroups(
            groups = groups,
            text = "text"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StackRowOfGroupsPreview() {
    StupidEnglishTheme {
        StackRowOfGroups(
            groups = listOf(
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
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupsStackRowPreview() {
    StupidEnglishTheme {
        GroupsStackRow(
            groups = listOf(
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
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OneGroupPreview() {
    StupidEnglishTheme {
        OneGroup(
            GroupItemUI(
                id = 1,
                name = "test name"
            )
        )
    }
}