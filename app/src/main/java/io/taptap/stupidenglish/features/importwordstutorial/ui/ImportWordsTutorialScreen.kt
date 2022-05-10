package io.taptap.stupidenglish.features.importwordstutorial.ui

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.uikit.AverageText
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
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

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ImportWordsTutorialContract.Effect.Navigation.BackToImportWords -> onNavigationRequested(
                    effect
                )
            }
        }?.collect()
    }

    StupidEnglishScaffold(
        scaffoldState = scaffoldState
    ) {
        StupidLanguageBackgroundBox { //todo стоит убрать в StupidEnglishScaffold?
            ContentScreen(
                state,
                onEventSent
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun ContentScreen(
    state: ImportWordsTutorialContract.State,
    onEventSent: (event: ImportWordsTutorialContract.Event) -> Unit
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            backgroundColor = Color.Transparent,
            // Our selected tab is our current page
            selectedTabIndex = pagerState.currentPage,
            // Override the indicator, using the provided pagerTabIndicatorOffset modifier
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
                        onEventSent(ImportWordsTutorialContract.Event.OnPageChosen(index))
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
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
                ImportWordsTutorialContract.Page.GOOGLE_TRANSLATER -> GoogleTranslaterTutorial()
                ImportWordsTutorialContract.Page.QUIZLET -> QuizletTutorial()
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
        AverageText(
            text = "1. Firstly, you'll need to open Google Spreadsheets and create a blank document",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ImportImage(
            painter = painterResource(R.drawable.googlesheet1)
        )
        AverageText(
            text = "2. Add 'word' to A1 cell and 'hint' to B1 cell",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ImportImage(
            painter = painterResource(R.drawable.googlesheet2)
        )
        AverageText(
            text = "3. You'll need to generate a public link to this spreasheet. Please click on the share button in the top right corner",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ImportImage(
            painter = painterResource(R.drawable.googlesheet3)
        )
        AverageText(
            text = "4. Give your list a name",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ImportImage(
            painter = painterResource(R.drawable.googlesheet4)
        )
        AverageText(
            text = "5. Change privacy to 'Public'",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ImportImage(
            painter = painterResource(R.drawable.googlesheet5)
        )
        AverageText(
            text = "6. Copy the link",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ImportImage(
            painter = painterResource(R.drawable.googlesheet6)
        )
        AverageText(
            text = "7. Paste the link",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ImportImage(
            painter = painterResource(R.drawable.sl_import)
        )
        AverageText(
            text = "8. Done",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun GoogleTranslaterTutorial() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(text = "Title")
//        Image(
//            painter = rememberImagePainter(data = )
//            //painter = rememberImagePainter(data = R.drawable.YOURBESTGIF, imageLoader = imgLoader),
//        )
        Text(
            color = Color.Green,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
        )

        Text(text = "Number1")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number2")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number3")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number4")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number5")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number6")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number7")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number8")


        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(
            color = Color.Red,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
        )
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(
            color = Color.Red,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
        )
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(
            color = Color.Red,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
        )
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(
            color = Color.Red,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
        )
    }
}

@Composable
private fun QuizletTutorial() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(text = "Title")
//        Image(
//            painter = rememberImagePainter(data = )
//            //painter = rememberImagePainter(data = R.drawable.YOURBESTGIF, imageLoader = imgLoader),
//        )
        Text(
            color = Color.Red,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
        )

        Text(text = "Number1")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number2")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number3")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number4")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number5")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number6")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number7")
        Image(
            painter = painterResource(R.drawable.ic_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

        Text(text = "Number8")


        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(
            color = Color.Red,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
        )
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(
            color = Color.Red,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
        )
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(
            color = Color.Red,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
        )
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
        Text(
            color = Color.Red,
            text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText"
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
