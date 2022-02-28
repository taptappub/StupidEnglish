package io.taptap.stupidenglish.features.words.ui

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.insets.ProvideWindowInsets
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.noRippleClickable
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.stupidenglish.features.words.ui.model.GroupItemUI
import io.taptap.stupidenglish.features.words.ui.model.GroupListModels
import io.taptap.stupidenglish.features.words.ui.model.NoGroupItemUI
import io.taptap.stupidenglish.features.words.ui.model.OnboardingWordUI
import io.taptap.stupidenglish.features.words.ui.model.WordListGroupUI
import io.taptap.stupidenglish.features.words.ui.model.WordListItemUI
import io.taptap.stupidenglish.features.words.ui.model.WordListListModels
import io.taptap.stupidenglish.features.words.ui.model.WordListTitleUI
import io.taptap.stupidenglish.ui.BottomSheetScreen
import io.taptap.stupidenglish.ui.Fab
import io.taptap.stupidenglish.ui.theme.Black200
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import io.taptap.stupidenglish.ui.theme.White100
import io.taptap.stupidenglish.ui.theme.getContentTextColor
import io.taptap.stupidenglish.ui.theme.getPrimaryButtonBackgroundColor
import io.taptap.stupidenglish.ui.theme.getSecondaryButtonBackgroundColor
import io.taptap.stupidenglish.ui.theme.getTitleTextColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun WordListScreen(
    context: Context,
    state: WordListContract.State,
    effectFlow: Flow<WordListContract.Effect>?,
    onEventSent: (event: WordListContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: WordListContract.Effect.Navigation) -> Unit
) {
    val scope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                onEventSent(WordListContract.Event.OnMotivationCancel)
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            MotivationBottomSheetScreen(
                onEventSent = onEventSent,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        },
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        ProvideWindowInsets {
            StupidEnglishTheme {
                val scaffoldState: ScaffoldState = rememberScaffoldState()

                // Listen for side effects from the VM
                LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
                    effectFlow?.onEach { effect ->
                        when (effect) {
                            is WordListContract.Effect.HideMotivation ->
                                modalBottomSheetState.hideSheet(scope)
                            is WordListContract.Effect.ShowMotivation ->
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
                            is WordListContract.Effect.ShowUnderConstruction -> {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.under_construction),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }?.collect()
                }

                Scaffold(
                    scaffoldState = scaffoldState,
                    backgroundColor = MaterialTheme.colors.background,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        val listState = rememberLazyListState()

                        MainList(
                            wordItems = state.wordList,
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
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun MainList(
    wordItems: List<WordListListModels>,
    onEventSent: (event: WordListContract.Event) -> Unit,
    listState: LazyListState,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
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
                is WordListTitleUI -> TitleItem(item = item)
                is OnboardingWordUI -> OnboardingItemRow(
                    onClicked = {
                        onEventSent(WordListContract.Event.OnOnboardingClick)
                    }
                )
                is WordListGroupUI -> GroupItemRow(
                    title = stringResource(id = item.titleRes),
                    button = stringResource(id = item.buttonRes),
                    list = item.groups,
                    onButtonClicked = {
                        onEventSent(WordListContract.Event.OnAddGroupClick)
                    },
                    onGroupClicked = { groupId ->
                        onEventSent(WordListContract.Event.OnGroupClick(groupId))
                    }
                )
            }
        }
    }
}

@Composable
private fun GroupItemRow(
    title: String,
    button: String,
    list: List<GroupListModels>,
    onButtonClicked: () -> Unit,
    onGroupClicked: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 28.dp)
    ) {
        GroupItemHeader(
            title = title,
            button = button,
            onButtonClicked = onButtonClicked
        )
        GroupItemGroupsRow(
            list = list,
            onGroupClicked = onGroupClicked
        )
    }
}

@Composable
private fun GroupItemGroupsRow(list: List<GroupListModels>, onGroupClicked: (Long) -> Unit) {
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        items(
            items = list,
            key = { it.id }
        ) { item ->
            when (item) {
                is NoGroupItemUI -> GroupItem(
                    title = stringResource(id = item.titleRes),
                    color = item.color,
                    onGroupClicked = onGroupClicked
                )
                is GroupItemUI -> GroupItem(
                    title = item.name,
                    color = item.color,
                    onGroupClicked = onGroupClicked
                )
            }
        }
    }
}

@Composable
private fun GroupItem(
    title: String,
    color: Color,
    onGroupClicked: (Long) -> Unit
) {
    Column(
        modifier = Modifier.clickable { onGroupClicked }
    ) {
        GroupIcon(
            letter = title[0],
            color = color,
            elevation = 8.dp
        )
        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = getContentTextColor(),
            style = MaterialTheme.typography.subtitle2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun GroupIcon(
    letter: Char,
    color: Color,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape, clip = false)
            .zIndex(elevation.value)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .background(
                color = color,
                shape = shape
            )
            .clip(shape)
    ) {
        Text(
            text = letter.toString(),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = getContentTextColor(),
            style = MaterialTheme.typography.subtitle2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun GroupItemHeader(title: String, button: String, onButtonClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Left,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1,
            color = getTitleTextColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = button,
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            color = MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.subtitle2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable {
                onButtonClicked
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun OnboardingItemRow(
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colors.secondary)
            .noRippleClickable(onClick = onClicked)
            .height(height = 140.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_main_onboarding),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(1f)
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
            Text(
                text = stringResource(id = R.string.app_name),
                textAlign = TextAlign.Left,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle1,
                color = White100,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(id = R.string.word_onboarding_text),
                textAlign = TextAlign.Left,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.subtitle1,
                color = White100,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TitleItem(
    item: WordListTitleUI
) {
    Text(
        text = stringResource(id = item.valueRes),
        textAlign = TextAlign.Left,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = getTitleTextColor(),
        style = MaterialTheme.typography.subtitle1,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
    )
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
            shape = RoundedCornerShape(12.dp),
            elevation = animateDpAsState(
                if (dismissState.dismissDirection != null) 4.dp else 0.dp
            ).value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                .clickable {
                    onClicked()
                }
        ) {
            Row {
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
}

@Composable
private fun WordItem(
    item: WordListItemUI,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = item.word,
            textAlign = TextAlign.Left,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1,
            color = getTitleTextColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = item.description,
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            color = getContentTextColor(),
            style = MaterialTheme.typography.subtitle2,
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
        CircularProgressIndicator(color = MaterialTheme.colors.secondary)
    }
}

@Composable
private fun MotivationBottomSheetScreen(
    modifier: Modifier,
    onEventSent: (event: WordListContract.Event) -> Unit
) {
    BottomSheetScreen(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_pen),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.Center)
                )
            }

            Text(
                text = stringResource(id = R.string.word_motivation_title),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(
                    Font(R.font.rubik_regular, FontWeight.Normal),
                    Font(R.font.rubik_medium, FontWeight.Medium),
                    Font(R.font.rubik_bold, FontWeight.Bold)
                ),
                color = getTitleTextColor(),
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            Text(
                text = stringResource(id = R.string.word_motivation_message),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = getContentTextColor(),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 44.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = getSecondaryButtonBackgroundColor()),
                    onClick = {
                        onEventSent(WordListContract.Event.OnMotivationDeclineClick)
                    }) {
                    Text(
                        text = stringResource(id = R.string.word_motivation_decline),
                        color = Black200,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = getPrimaryButtonBackgroundColor()),
                    onClick = {
                        onEventSent(WordListContract.Event.OnMotivationConfirmClick)
                    }) {
                    Text(
                        text = stringResource(id = R.string.word_motivation_confirm),
                        color = White100
                    )
                }
            }
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
            onButtonClicked = {},
            list = emptyList(),
            onGroupClicked = {}
        )
    }
}