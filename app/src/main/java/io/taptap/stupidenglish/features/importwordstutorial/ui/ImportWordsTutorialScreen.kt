package io.taptap.stupidenglish.features.importwordstutorial.ui

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.uikit.AverageText
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalPagerApi::class
)
@Composable
fun ImportWordsTutorialScreen(
    context: Context,
    state: ImportWordsTutorialContract.State,
    effectFlow: Flow<ImportWordsTutorialContract.Effect>?,
    onEventSent: (event: ImportWordsTutorialContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: ImportWordsTutorialContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ImportWordsTutorialContract.Effect.Navigation.BackToImportWords -> onNavigationRequested(
                    effect
                )
                is ImportWordsTutorialContract.Effect.ScrollToPage -> {
                    scope.launch {
                        pagerState.scrollToPage(effect.index)
                    }
                }
            }
        }?.collect()
    }

    StupidEnglishScaffold(
        scaffoldState = scaffoldState
    ) {
        StupidLanguageBackgroundBox { //todo стоит убрать в StupidEnglishScaffold?
            ContentScreen(
                state = state,
                pagerState = pagerState,
                scope = scope,
                onEventSent = onEventSent
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun ContentScreen(
    state: ImportWordsTutorialContract.State,
    pagerState: PagerState,
    scope: CoroutineScope,
    onEventSent: (event: ImportWordsTutorialContract.Event) -> Unit
) {
    LaunchedEffect(pagerState) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
            onEventSent(ImportWordsTutorialContract.Event.OnPageChosen(pageIndex))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            backgroundColor = Color.Transparent,
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            // Add tabs for all of our pages
            state.pages.forEachIndexed { index, page ->
                val tint by animateColorAsState(
                    if (page.isSelected) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )

                Tab(
                    text = {
                        Text(
                            textAlign = TextAlign.Center,
                            text = stringResource(id = page.titleRes),
                            color = tint,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .align(CenterHorizontally)
                                .padding(6.dp)
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        onEventSent(ImportWordsTutorialContract.Event.ScrollToPage(index))
                    }
                )
            }
        }

        HorizontalPager(
            count = state.pages.size,
            state = pagerState,
        ) { pageIndex ->
            when (ImportWordsTutorialContract.Page.getByIndex(pageIndex)) {
                ImportWordsTutorialContract.Page.GOOGLE_SHEET -> GoogleSheetTutorial()
                ImportWordsTutorialContract.Page.GOOGLE_TRANSLATER -> GoogleTranslaterTutorial(
                    onEventSent = onEventSent
                )
                //ImportWordsTutorialContract.Page.QUIZLET -> QuizletTutorial()
            }
        }
    }
}

@Composable
private fun GoogleSheetTutorial() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        ImportText(text = stringResource(id = R.string.imwt_google_sheet_1))
        ImportImage(
            painter = painterResource(R.drawable.googlesheet1)
        )
        ImportText(text = stringResource(id = R.string.imwt_google_sheet_2))
        ImportImage(
            painter = painterResource(R.drawable.googlesheet2)
        )
        ImportText(text = stringResource(id = R.string.imwt_google_sheet_3))
        ImportImage(
            painter = painterResource(R.drawable.googlesheet3)
        )
        ImportText(text = stringResource(id = R.string.imwt_google_sheet_4))
        ImportImage(
            painter = painterResource(R.drawable.googlesheet4)
        )
        ImportText(text = stringResource(id = R.string.imwt_google_sheet_5))
        ImportImage(
            painter = painterResource(R.drawable.googlesheet5)
        )
        ImportText(text = stringResource(id = R.string.imwt_google_sheet_6))
        ImportImage(
            painter = painterResource(R.drawable.googlesheet6)
        )
        ImportText(text = stringResource(id = R.string.imwt_google_sheet_7))
        ImportImage(
            painter = painterResource(R.drawable.sl_import)
        )
        ImportText(text = stringResource(id = R.string.imwt_google_sheet_8))
    }
}

@Composable
private fun GoogleTranslaterTutorial(
    onEventSent: (event: ImportWordsTutorialContract.Event) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        ImportText(text = stringResource(id = R.string.imwt_google_translater_1))
        ImportImage(
            painter = painterResource(R.drawable.gt1)
        )
        ImportText(text = stringResource(id = R.string.imwt_google_translater_2))
        ImportImage(
            painter = painterResource(R.drawable.gt2)
        )
        ImportText(text = stringResource(id = R.string.imwt_google_translater_3))
        ImportImage(
            painter = painterResource(R.drawable.gt3)
        )
        AverageText(
            text = buildAnnotatedString {
                append(stringResource(id = R.string.imwt_google_translater_41))
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    append(stringResource(id = R.string.imwt_google_translater_42))
                }
            },
            maxLines = 5,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable {
                    val pageIndex = ImportWordsTutorialContract.Page.GOOGLE_SHEET.index
                    onEventSent(ImportWordsTutorialContract.Event.ScrollToPage(pageIndex))
                }
        )
    }
}

@Composable
private fun ColumnScope.ImportImage(
    painter: Painter,
    modifier: Modifier = Modifier
        .align(CenterHorizontally)
        .padding(bottom = 8.dp)
        .clip(RoundedCornerShape(12.dp))
        .border(
            width = 1.dp,
            color = Color.Gray,
            shape = RoundedCornerShape(12.dp)
        )
) {
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
private fun ImportText(
    text: String
) {
    AverageText(
        text = text,
        textAlign = TextAlign.Start,
        maxLines = 5,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
