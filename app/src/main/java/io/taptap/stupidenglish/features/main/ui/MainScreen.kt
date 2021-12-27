package io.taptap.stupidenglish.features.main.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract
import io.taptap.stupidenglish.features.sentences.ui.SentencesListScreen
import io.taptap.stupidenglish.features.sentences.ui.SentencesListViewModel
import io.taptap.stupidenglish.features.words.ui.WordListContract
import io.taptap.stupidenglish.features.words.ui.WordListScreen
import io.taptap.stupidenglish.features.words.ui.WordListViewModel
import io.taptap.stupidenglish.ui.theme.getIndicatorActiveColor
import io.taptap.stupidenglish.ui.theme.getIndicatorInactiveColor
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalPagerApi
@Composable
fun MainScreen(
    state: MainContract.State,
    effectFlow: Flow<MainContract.Effect>,
    onEventSent: (event: MainContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit,
    navController: NavHostController
) {
    if (state.pagerIsVisible) {
        WordListDestination(navController = navController)
    } else {
        MainScreenWithoutPager(
            state = state,
            navController = navController
        )
    }
}

@InternalCoroutinesApi
@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalPagerApi
@Composable
private fun MainScreenWithoutPager(
    state: MainContract.State,
    navController: NavHostController
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    scope.launch {
        pagerState.scrollToPage(state.pageId)
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            HorizontalPagerIndicator(
                indicatorWidth = 10.dp,
                indicatorHeight = 10.dp,
                inactiveColor = getIndicatorInactiveColor(),
                activeColor = getIndicatorActiveColor(),
                pagerState = pagerState,
                spacing = 12.dp,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        HorizontalPager(
            count = 2,
            state = pagerState
        ) { page ->
            when (page) {
                0 -> WordListDestination(navController = navController)
                1 -> SentenceListDestination(navController = navController)
            }
        }
    }
}

@Composable
private fun WordListDestination(
    navController: NavHostController
) {
    val wordViewModel: WordListViewModel = hiltViewModel()
    val wordState = wordViewModel.viewState.value

    WordListScreen(
        context = LocalContext.current,
        state = wordState,
        effectFlow = wordViewModel.effect,
        onEventSent = { event -> wordViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is WordListContract.Effect.Navigation.ToAddWord -> {
                    navController.navigate(NavigationKeys.Route.SE_ADD_WORD)
                }
                is WordListContract.Effect.Navigation.ToAddSentence -> {
                    val json =
                        Uri.encode(Gson().toJson(navigationEffect.sentenceNavigation))
                    navController.navigate("${NavigationKeys.Route.SE_SENTENCES_LIST}/${json}")
                }
            }
        })
}

@Composable
private fun SentenceListDestination(
    navController: NavHostController
) {
    val sentenceViewModel: SentencesListViewModel = hiltViewModel()
    val sentenceState = sentenceViewModel.viewState.value

    SentencesListScreen(
        context = LocalContext.current,
        state = sentenceState,
        effectFlow = sentenceViewModel.effect,
        onEventSent = { event -> sentenceViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is SentencesListContract.Effect.Navigation.ToAddSentence) {
                val json =
                    Uri.encode(Gson().toJson(navigationEffect.sentenceNavigation))
                navController.navigate("${NavigationKeys.Route.SE_SENTENCES_LIST}/${json}")
            }
        })
}