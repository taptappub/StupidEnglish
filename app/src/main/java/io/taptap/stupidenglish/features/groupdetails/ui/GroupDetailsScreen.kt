package io.taptap.stupidenglish.features.groupdetails.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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