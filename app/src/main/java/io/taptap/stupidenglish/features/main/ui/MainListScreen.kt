package io.taptap.stupidenglish.features.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import com.google.accompanist.insets.ProvideWindowInsets
import io.taptap.stupidenglish.ui.theme.getContentTextColor
import io.taptap.stupidenglish.ui.theme.getTitleTextColor
import kotlinx.coroutines.flow.onEach


@Composable
fun MainListScreen(
    state: MainListContract.State,
    effectFlow: Flow<MainListContract.Effect>?,
    onEventSent: (event: MainListContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: MainListContract.Effect.Navigation) -> Unit
) {
    ProvideWindowInsets {
        StupidEnglishTheme {
            val scaffoldState: ScaffoldState = rememberScaffoldState()

            // Listen for side effects from the VM
            LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
                effectFlow?.onEach { effect ->
                    when (effect) {
                        is MainListContract.Effect.DataWasLoaded ->
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Food categories are loaded.",
                                duration = SnackbarDuration.Short
                            )
                        is MainListContract.Effect.Navigation.ToAddWord -> onNavigationRequested(
                            effect
                        )
                    }
                }?.collect()
            }

            Scaffold(
                scaffoldState = scaffoldState,
                backgroundColor = MaterialTheme.colors.background,
            ) {
//        backgroundColor = MaterialTheme.colors.surface,
                Box {
//            FoodCategoriesList(wordItems = state.categories) { itemId ->
//                onEventSent(MainListContract.Event.CategorySelection(itemId))
//            }
                    MainList(
                        wordItems = state.mainList,
                        onEventSent = onEventSent
                    )
                    if (state.isLoading) {
                        LoadingBar()
                    }
                }
            }
        }
    }

    /*val scaffoldState: ScaffoldState = rememberScaffoldState()

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is MainListContract.Effect.DataWasLoaded ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Food categories are loaded.",
                        duration = SnackbarDuration.Short
                    )
                is MainListContract.Effect.Navigation.ToCategoryDetails -> onNavigationRequested(
                    effect
                )
            }
        }?.collect()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.surface,
    ) {
//        backgroundColor = MaterialTheme.colors.surface,
        Box {
//            FoodCategoriesList(wordItems = state.categories) { itemId ->
//                onEventSent(MainListContract.Event.CategorySelection(itemId))
//            }
            MainList(wordItems = state.mainList)
            if (state.isLoading) {
                LoadingBar()
            }
        }
    }*/

}

@Composable
fun MainList(
    wordItems: List<MainListListModels>,
    onEventSent: (event: MainListContract.Event) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(wordItems) { item ->
            when (item) {
                is NewWordUI -> NewWordItemRow(item = item) {
                    onEventSent(MainListContract.Event.OnAddWordClick)
                }
                is WordListItemUI -> WordItemRow(item = item)
                is WordListTitleUI -> TitleItem(item = item)
            }
        }
    }
}

@Composable
fun NewWordItemRow(
    item: NewWordUI,
    onItemClicked: () -> Unit
) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
            .clickable { onItemClicked() },
        elevation = 0.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row {
            NewWordItem(
                item = item,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 22.dp,
                        bottom = 22.dp
                    )
                    .fillMaxWidth(0.80f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun TitleItem(
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

@Composable
fun WordItemRow(
    item: WordListItemUI
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
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

@Composable
fun NewWordItem(
    item: NewWordUI,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = item.valueRes),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = getContentTextColor(),
            style = MaterialTheme.typography.subtitle1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
//        if (item.description?.trim()?.isNotEmpty() == true)
//            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
//                Text(
//                    text = item.description.trim(),
//                    textAlign = TextAlign.Start,
//                    overflow = TextOverflow.Ellipsis,
//                    style = MaterialTheme.typography.caption,
//                    maxLines = expandedLines
//                )
//            }
    }
}

@Composable
fun WordItem(
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
//        if (item.description?.trim()?.isNotEmpty() == true)
//            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
//                Text(
//                    text = item.description.trim(),
//                    textAlign = TextAlign.Start,
//                    overflow = TextOverflow.Ellipsis,
//                    style = MaterialTheme.typography.caption,
//                    maxLines = expandedLines
//                )
//            }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StupidEnglishTheme {
        MainListScreen(
            MainListContract.State(),
            null,
            { },
            { })
    }
}