package io.taptap.stupidenglish.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.taptap.stupidenglish.R
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListModels
import io.taptap.uikit.group.NoGroup
import io.taptap.uikit.AverageTitle
import io.taptap.uikit.BottomSheetScreen
import io.taptap.uikit.PrimaryButton
import io.taptap.uikit.group.GroupListItemsModels
import io.taptap.uikit.theme.StupidEnglishTheme

@Composable
fun ChooseGroupBottomSheetScreen(
    list: List<GroupListItemsModels>,
    selectedList: List<GroupListItemsModels>,
    @StringRes titleRes: Int,
    @StringRes buttonRes: Int,
    modifier: Modifier,
    onItemClick: (GroupListItemsModels) -> Unit,
    onButtonClick: () -> Unit
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

            ChooseGroupContent(
                list = list,
                selectedList = selectedList,
                onItemClick = onItemClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )

            PrimaryButton(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                onClick = onButtonClick,
                text = stringResource(id = buttonRes),
            )
        }
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
