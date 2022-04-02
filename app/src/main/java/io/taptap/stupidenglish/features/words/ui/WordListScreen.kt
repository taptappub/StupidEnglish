package io.taptap.stupidenglish.features.words.ui

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.logic.groups.GroupItemUI
import io.taptap.stupidenglish.base.logic.groups.GroupListModels
import io.taptap.stupidenglish.base.logic.groups.NoGroup
import io.taptap.stupidenglish.base.logic.groups.NoGroupItemUI
import io.taptap.stupidenglish.base.noRippleClickable
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.stupidenglish.features.words.ui.model.*
import io.taptap.stupidenglish.ui.ChooseGroupBottomSheetScreen
import io.taptap.uikit.*
import io.taptap.uikit.theme.StupidEnglishTheme
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import io.taptap.uikit.theme.getStupidLanguageBackgroundRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun WordListScreen(
    context: Context,
    state: WordListContract.State,
    effectFlow: Flow<WordListContract.Effect>?,
    onEventSent: (event: WordListContract.Event) -> Unit,
    onChangeBottomSheetVisibility: (visibility: Boolean) -> Unit,
    onNavigationRequested: (navigationEffect: WordListContract.Effect.Navigation) -> Unit
) {
    val scope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        val keyboardController = LocalSoftwareKeyboardController.current
        DisposableEffect(Unit) {
            onDispose {
                when (state.sheetContentType) {
                    WordListContract.SheetContentType.AddGroup ->
                        onEventSent(WordListContract.Event.OnGroupAddingCancel)
                    WordListContract.SheetContentType.Motivation ->
                        onEventSent(WordListContract.Event.OnMotivationCancel)
                    WordListContract.SheetContentType.RemoveGroup ->
                        onEventSent(WordListContract.Event.OnGroupRemovingCancel)
                }
                keyboardController?.hide()
            }
        }
    }
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            when (state.sheetContentType) {
                WordListContract.SheetContentType.AddGroup ->
                    AddGroupBottomSheetScreen(
                        state = state,
                        onEventSent = onEventSent,
                        onAddGroup = {
                            if (state.group.isNotEmpty()) {
                                onEventSent(WordListContract.Event.OnApplyGroup)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                            .height(300.dp)
                    )
                WordListContract.SheetContentType.Motivation ->
                    DialogSheetScreen(
                        painter = painterResource(R.drawable.ic_pen),
                        title = stringResource(id = R.string.word_motivation_title),
                        message = stringResource(id = R.string.word_motivation_message),
                        okButtonText = stringResource(id = R.string.word_motivation_confirm),
                        cancelButtonText = stringResource(id = R.string.word_motivation_decline),
                        modifier = Modifier.fillMaxWidth(),
                        onOkButtonClick = {
                            onEventSent(WordListContract.Event.OnMotivationConfirmClick)
                        },
                        onCancelButtonClick = {
                            onEventSent(WordListContract.Event.OnMotivationDeclineClick)
                        }
                    )
                WordListContract.SheetContentType.RemoveGroup ->
                    ChooseGroupBottomSheetScreen(
                        list = state.dialogGroups,
                        selectedList = state.removedGroups,
                        buttonRes = R.string.word_remove_group_button,
                        titleRes = R.string.word_remove_group_title,
                        onItemClick = { item ->
                            onEventSent(WordListContract.Event.OnGroupSelect(item))
                        },
                        onButtonClick = {
                            onEventSent(WordListContract.Event.OnApplyGroupsRemove)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    )
            }
        },
    ) {
        StupidEnglishTheme {
            val scaffoldState: ScaffoldState = rememberScaffoldState()

            // Listen for side effects from the VM
            LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
                effectFlow?.onEach { effect ->
                    when (effect) {
                        is WordListContract.Effect.HideBottomSheet ->
                            modalBottomSheetState.hideSheet(scope)
                        is WordListContract.Effect.ShowBottomSheet ->
                            modalBottomSheetState.showSheet(scope)
                        is WordListContract.Effect.Navigation.ToAddWord ->
                            onNavigationRequested(effect)
                        is WordListContract.Effect.GetRandomWordsError ->
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = context.getString(effect.errorRes),
                                duration = SnackbarDuration.Short
                            )
                        is WordListContract.Effect.GetWordsError ->
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = context.getString(effect.errorRes),
                                duration = SnackbarDuration.Short
                            )
                        is WordListContract.Effect.Navigation.ToAddSentence ->
                            onNavigationRequested(effect)
                        is WordListContract.Effect.ShowUnderConstruction ->
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = context.getString(R.string.under_construction),
                                duration = SnackbarDuration.Short
                            )
                        is WordListContract.Effect.ChangeBottomBarVisibility -> {
                            onChangeBottomSheetVisibility(effect.isShown)
                        }
                    }
                }?.collect()
            }

            Scaffold(
                scaffoldState = scaffoldState
            ) {
                StupidLanguageBackgroundBox {
                    val listState = rememberLazyListState()

                    MainList(
                        wordItems = state.wordList,
                        group = state.currentGroup,
                        listState = listState,
                        onEventSent = onEventSent
                    )
                    if (state.isLoading) {
                        LoadingBar()
                    }
                    Fab(
                        extended = listState.firstVisibleItemIndex == 0,
                        modifier = Modifier.align(Alignment.BottomEnd),
                        iconRes = R.drawable.ic_plus,
                        text = stringResource(id = R.string.word_fab_text),
                        onFabClicked = { onEventSent(WordListContract.Event.OnAddWordClick) }
                    )
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun MainList(
    wordItems: List<WordListListModels>,
    group: GroupListModels?,
    listState: LazyListState,
    onEventSent: (event: WordListContract.Event) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            top = WindowInsets.navigationBars.getTop(LocalDensity.current).dp,
            bottom = WindowInsets.navigationBars.getBottom(LocalDensity.current).dp
        )
    ) {
        items(
            items = wordItems,
            key = { it.id }
        ) { item ->
            val dismissState = rememberDismissState()
            if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                onEventSent(WordListContract.Event.OnWordDismiss(item as WordListItemUI))
            }

            when (item) {
                is WordListItemUI -> WordItemRow(
                    item = item,
                    modifier = Modifier.animateItemPlacement(),
                    dismissState = dismissState,
                    onClicked = {
                        onEventSent(WordListContract.Event.OnWordClick)
                    }
                )
                is WordListTitleUI -> AverageTitle(
                    text = stringResource(id = item.valueRes),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )
                is OnboardingWordUI -> OnboardingItemRow(
                    onClicked = {
                        onEventSent(WordListContract.Event.OnOnboardingClick)
                    }
                )
                is WordListGroupUI -> GroupItemRow(
                    title = stringResource(id = item.titleRes),
                    button = stringResource(id = item.buttonRes),
                    list = item.groups,
                    currentGroup = group,
                    onButtonClicked = {
                        onEventSent(WordListContract.Event.OnAddGroupClick)
                    },
                    onGroupClicked = { group ->
                        onEventSent(WordListContract.Event.OnGroupClick(group))
                    },
                    onGroupLongClicked = { group ->
                        onEventSent(WordListContract.Event.OnGroupLongClick(group))
                    }
                )
                is WordListEmptyUI -> EmptyListContent(
                    title = stringResource(id = R.string.word_empty_list_title),
                    description = stringResource(id = item.descriptionRes),
                    modifier = Modifier.height(300.dp)
                )
            }
        }
    }
}

@Composable
private fun GroupItemRow(
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
            modifier = Modifier.size(56.dp)
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
            fontSize = 12.sp,
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.secondary
            },
            style = if (selected) {
                MaterialTheme.typography.bodyLarge
            } else {
                MaterialTheme.typography.bodyMedium
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        )
    }
}

@Composable
private fun GroupItemHeader(title: String, button: String, onButtonClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AverageTitle(
            text = title,
            maxLines = 1,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = button,
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable {
                onButtonClicked()
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun OnboardingItemRow(
    onClicked: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .noRippleClickable(onClick = onClicked)
            .height(height = 140.dp)
    ) {
        Row(modifier = Modifier
            .background(getStupidLanguageBackgroundRow())
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_main_onboarding),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .wrapContentWidth()
                    .height(height = 140.dp)
            )
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth()
                    .fillMaxHeight()
                    .padding(
                        top = 28.dp,
                        bottom = 28.dp,
                        end = 12.dp
                    )
            ) {
                LargeTitle(
                    text = stringResource(id = R.string.app_name),
                    textAlign = TextAlign.Left,
                )
                Text(
                    text = stringResource(id = R.string.word_onboarding_text),
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun WordItemRow(
    item: WordListItemUI,
    onClicked: () -> Unit,
    dismissState: DismissState,
    modifier: Modifier
) {
    SwipeToDismiss(
        state = dismissState,
        dismissThresholds = { direction ->
            FractionalThreshold(if (direction == DismissDirection.StartToEnd) 0.25f else 0.5f)
        },
        modifier = modifier
            .padding(vertical = 1.dp),
        directions = setOf(DismissDirection.StartToEnd),
        background = {
            val scale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == Default) 0.6f else 2.2f
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "icon",
                    modifier = Modifier.scale(scale)
                )
            }
        }
    ) {
        Card(
            backgroundColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            elevation = animateDpAsState(
                if (dismissState.dismissDirection != null) 8.dp else 4.dp
            ).value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                .clickable {
                    onClicked()
                }
        ) {
            WordItem(
                item = item,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
                    .fillMaxWidth(0.80f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun WordItem(
    item: WordListItemUI,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        AverageTitle(
            text = item.word,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = item.description,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun LoadingBar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddGroupBottomSheetScreen(
    modifier: Modifier,
    onAddGroup: () -> Unit,
    onEventSent: (event: WordListContract.Event) -> Unit,
    state: WordListContract.State
) {
    BottomSheetScreen(
        modifier = modifier
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (title, content, button) = createRefs()
            val keyboardController = LocalSoftwareKeyboardController.current

            Box(
                modifier = modifier.constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                val focusRequester = FocusRequester()

                AddTextField(
                    value = state.group,
                    onValueChange = { onEventSent(WordListContract.Event.OnGroupChanging(it)) },
                    placeholder = "",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            onAddGroup()
                        }
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .align(Alignment.Center)
                )

                DisposableEffect(Unit) {
                    focusRequester.requestFocus()
                    onDispose { }
                }
            }
            AverageTitle(
                text = stringResource(id = R.string.word_group_add_group_title),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
            )

            NextButton(
                visibility = state.group.isNotEmpty(),
                onClick = {
                    keyboardController?.hide()
                    onAddGroup()
                },
                modifier = Modifier.constrainAs(button) {
                    bottom.linkTo(parent.bottom, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupItemHeader() {
    StupidEnglishTheme {
        GroupItemHeader(
            title = "Groups",
            button = "Add",
            onButtonClicked = {}
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
                titleRes = R.string.word_group_title
            ),
            selected = true,
            onGroupClicked = {},
            onGroupLongClicked = {}
        )
    }
}
