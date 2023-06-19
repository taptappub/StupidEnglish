package io.taptap.stupidenglish.features.groupdetails.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.base.model.WordWithGroups
import io.taptap.stupidenglish.features.groupdetails.ui.GroupDetailsContract.*
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsButtonUI
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsDynamicTitleUI
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsEmptyUI
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsUIModel
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsWordItemUI
import io.taptap.stupidenglish.features.groups.ui.LoadingBar
import io.taptap.uikit.AverageTitle
import io.taptap.uikit.EmptyListContent
import io.taptap.uikit.SecondaryButton
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.StupidEnglishTopAppBar
import io.taptap.uikit.complex.WordItemRow
import io.taptap.uikit.fab.BOTTOM_BAR_MARGIN
import io.taptap.uikit.group.getTitle
import io.taptap.uikit.theme.StupidEnglishTheme
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlin.math.absoluteValue


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun GroupDetailsScreen(
    context: Context,
    state: State,
    mainList: List<GroupDetailsUIModel>,
    effectFlow: Flow<Effect>?,
    onEventSent: (event: Event) -> Unit,
    onNavigationRequested: (navigationEffect: Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is Effect.GetWordsError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(effect.errorRes),
                        duration = SnackbarDuration.Short
                    )

                is Effect.ShowRecover -> {
                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(R.string.grdt_delete_message),
                        duration = SnackbarDuration.Short,
                        actionLabel = context.getString(R.string.grdt_recover)
                    )
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> onEventSent(Event.OnApplyDismiss)
                        SnackbarResult.ActionPerformed -> onEventSent(Event.OnRecover)
                    }
                }

                is Effect.Navigation.BackTo -> onNavigationRequested(effect)
                is Effect.Navigation.ToAddSentence -> onNavigationRequested(effect)
                is Effect.Navigation.ToAddWordWithGroup -> onNavigationRequested(effect)
                is Effect.Navigation.ToFlashCards -> onNavigationRequested(effect)
            }
        }?.collect()
    }

    BackHandler {
        onEventSent(Event.OnBackClick)
    }

    StupidEnglishScaffold(
        scaffoldState = scaffoldState
    ) {
        ContentScreen(
            state,
            mainList,
            onEventSent
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ContentScreen(
    state: State,
    mainList: List<GroupDetailsUIModel>,
    onEventSent: (event: Event) -> Unit
) {
    StupidLanguageBackgroundBox(
        topbar = {
            StupidEnglishTopAppBar(
                text = "",
                onNavigationClick = { onEventSent(Event.OnBackClick) },
            )
        }
    ) {
        val listState = rememberLazyListState()

        MainList(
            groupItems = mainList,
            deletedWords = state.deletedWords,
            listState = listState,
            onEventSent = onEventSent
        )
        if (state.isLoading) {
            LoadingBar()
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun MainList(
    groupItems: List<GroupDetailsUIModel>,
    deletedWords: List<WordWithGroups>,
    listState: LazyListState,
    onEventSent: (event: Event) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            top = WindowInsets.navigationBars.getTop(LocalDensity.current).dp,
            bottom = WindowInsets.navigationBars.getBottom(LocalDensity.current).dp + 12.dp + BOTTOM_BAR_MARGIN
        )
    ) {
        items(
            items = groupItems,
            key = { it.id }
        ) { item ->
            val dismissState = rememberDismissState()
            val canBeRecovered = deletedWords.map { it.word.id }.contains(item.id)
            if (canBeRecovered) { //todo выделить в отдельный класс с возможностью удалять?
                if (dismissState.currentValue != DismissValue.Default) {
                    LaunchedEffect(Unit) {
                        dismissState.reset()
                        onEventSent(Event.OnRecovered(item as GroupDetailsWordItemUI))
                    }
                }
            } else {
                if (item is GroupDetailsWordItemUI) {
                    if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                        onEventSent(Event.OnDismiss(item))
                    }
                }
            }

            when (item) {
                is GroupDetailsButtonUI -> SecondaryButton(
                    text = stringResource(id = item.valueRes),
                    onClick = {
                        when (item.buttonId) {
                            ButtonId.remove -> onEventSent(Event.OnRemoveGroupClick)
                            ButtonId.flashcards -> onEventSent(Event.ToFlashCards)
                            ButtonId.learn -> onEventSent(Event.ToAddSentence)
                            ButtonId.addWord -> onEventSent(Event.OnAddWordClick)
                        }
                    }
                )

                is GroupDetailsDynamicTitleUI -> AverageTitle(
                    text = item.currentGroup.getTitle(),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )

                is GroupDetailsEmptyUI -> EmptyListContent(
                    title = stringResource(id = R.string.word_empty_list_title),
                    description = stringResource(id = item.descriptionRes),
                    modifier = Modifier.height(300.dp)
                )

                is GroupDetailsWordItemUI -> WordItemRow(
                    word = item.word,
                    description = item.description,
                    modifier = Modifier.animateItemPlacement(),
                    dismissState = dismissState,
                    onClicked = {}
                )
            }
        }
    }
}


@Preview
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun MainListPreview() {
    StupidEnglishTheme {
        MainList(
            groupItems = listOf(
                GroupDetailsDynamicTitleUI(GroupItemUI(0, "Group name")),
                //
                GroupDetailsButtonUI(ButtonId.addWord, R.string.grdt_add_word),
                GroupDetailsButtonUI(ButtonId.learn, R.string.grdt_learn),
                GroupDetailsButtonUI(ButtonId.remove, R.string.grdt_remove),
                GroupDetailsButtonUI(ButtonId.flashcards, R.string.grdt_flashcards),
                GroupDetailsButtonUI(ButtonId.share, R.string.grdt_share),
                //
                GroupDetailsWordItemUI(id = 0, word = "Word", description = "Description"),
                GroupDetailsWordItemUI(id = 1, word = "Word", description = "Description"),
                GroupDetailsWordItemUI(id = 2, word = "Word", description = "Description"),
                GroupDetailsWordItemUI(id = 3, word = "Word", description = "Description"),
            ),
            deletedWords = listOf(
                WordWithGroups(
                    word = Word(4, "Deleted word", "desciption", 0),
                    groups = listOf()
                )
            ),
            listState = rememberLazyListState(),
            onEventSent = {},
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordPager(
    words: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState {
        words.size
    }

    val singlePageWithNeighbourEdges = object : PageSize {
        private val edgesWidth = 24.dp

        override fun Density.calculateMainAxisPageSize(availableSpace: Int, pageSpacing: Int): Int =
            availableSpace - edgesWidth.roundToPx()
    }

    Column {
        val pagerHeight = 220.dp
        val pageHeight = 200.dp

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

        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(words.size) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
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

@Preview
@Composable
fun WordPagerPreview() {
    StupidEnglishTheme {
        WordPager(
            listOf(
                "Баклан" to "Человек, не разбирающийся в вопросе",
                "Ёкать" to "Издавать от неожиданности неопределенные отрывистые звуки",
                "Ёрничать" to "Озорничать, допускать колкости по отношению к другим",
                "Изюбрь" to "Грациозный благородный олень",
            )
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RotatablePage(
    word: Pair<String, String>,
    modifier: Modifier = Modifier,
) {
    val (text, description) = word
    var angle by remember {
        mutableFloatStateOf(0f)
    }

    val rotation = animateFloatAsState(
        targetValue = angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        ),
        label = "Page rotation"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationX = rotation.value
            },
        onClick = {
            angle = (angle + 180) % 360
        }
    ) {
        Box(
            modifier = Modifier.graphicsLayer {
                //rotate content back
                rotationX = -rotation.value
            }
        ) {
            Text(
                text = if (rotation.value > 90f) description else text,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(all = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun RotatablePagePreview() {
    StupidEnglishTheme {
        RotatablePage(
            word = "Баклан" to "Человек, не разбирающийся в вопросе",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}