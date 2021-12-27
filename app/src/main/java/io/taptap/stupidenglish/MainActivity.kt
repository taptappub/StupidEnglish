package io.taptap.stupidenglish

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceContract
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceScreen
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceViewModel
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
import io.taptap.stupidenglish.features.addword.ui.AddWordScreen
import io.taptap.stupidenglish.features.addword.ui.AddWordViewModel
import io.taptap.stupidenglish.features.sentences.navigation.SentenceNavigationNavType
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract
import io.taptap.stupidenglish.features.sentences.ui.SentencesListScreen
import io.taptap.stupidenglish.features.sentences.ui.SentencesListViewModel
import io.taptap.stupidenglish.features.words.ui.WordListContract
import io.taptap.stupidenglish.features.words.ui.WordListScreen
import io.taptap.stupidenglish.features.words.ui.WordListViewModel
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import io.taptap.stupidenglish.ui.theme.getIndicatorActiveColor
import io.taptap.stupidenglish.ui.theme.getIndicatorInactiveColor

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalMaterialNavigationApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StupidEnglishTheme {
                StupidApp()
            }
        }
    }

    @ExperimentalMaterialNavigationApi
    @ExperimentalPagerApi
    @Composable
    private fun StupidApp() {
        val bottomSheetNavigator = rememberBottomSheetNavigator()
        val navController = rememberAnimatedNavController()
        navController.navigatorProvider += bottomSheetNavigator
        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier
                .wrapContentHeight()
        ) {
            AnimatedNavHost(navController, startDestination = NavigationKeys.Route.SE_LIST) {
                composable(route = NavigationKeys.Route.SE_LIST) {
                    MainListDestination(navController)
                }
                composable(route = NavigationKeys.Route.SE_ADD_WORD,
                    enterTransition = {
                        slideInVertically(initialOffsetY = { 1000 }/*, animationSpec = tween(700)*/)
                    },
                    exitTransition = {
                        slideOutVertically(targetOffsetY = { 1000 }/*, animationSpec = tween(700)*/)
                    }) {
                    AddWordDialogDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_ADD_SENTENCE,
                    arguments = listOf(
                        navArgument(NavigationKeys.Arg.SENTENCE_WORDS_ID) {
                            type = SentenceNavigationNavType()
                        }
                    )
                ) {
                    AddSentenceDialogDestination(navController)
                }

//                bottomSheet(route = NavigationKeys.Route.SE_ADD_WORD) {
//                    AddWordDialogDestination(navController)
//                }

//                bottomSheet(
//                    route = NavigationKeys.Route.SE_ADD_SENTENCE,
//                    arguments = listOf(
//                        navArgument(NavigationKeys.Arg.SENTENCE_WORDS_ID) {
//                            type = SentenceNavigationNavType()
//                        }
//                    )
//                ) {
//                    AddSentenceDialogDestination(navController)
//                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun AddWordDialogDestination(
    navController: NavHostController
) {
    val addWordViewModel: AddWordViewModel = hiltViewModel()
    val addWordState = addWordViewModel.viewState.value

    AddWordScreen(
        context = LocalContext.current,
        state = addWordState,
        effectFlow = addWordViewModel.effect,
        onEventSent = { event -> addWordViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is AddWordContract.Effect.Navigation.BackToWordList) {
                navController.navigateUp()
            }
        })
}

@Composable
private fun AddSentenceDialogDestination(navController: NavHostController) {
    val addSentenceViewModel: AddSentenceViewModel = hiltViewModel()
    val addSentenceState = addSentenceViewModel.viewState.value

    AddSentenceScreen(
        context = LocalContext.current,
        state = addSentenceState,
        effectFlow = addSentenceViewModel.effect,
        onEventSent = { event -> addSentenceViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is AddSentenceContract.Effect.Navigation.BackToSentenceList) {
                navController.navigate(NavigationKeys.Route.SE_LIST) //todo надо будет переключить на верный таб
            }
        })
}

@ExperimentalPagerApi
@Composable
private fun MainListDestination(navController: NavHostController) {
    val wordViewModel: WordListViewModel = hiltViewModel()
    val wordState = wordViewModel.viewState.value

    val sentenceViewModel: SentencesListViewModel = hiltViewModel()
    val sentenceState = sentenceViewModel.viewState.value
    val pagerState = rememberPagerState()

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
                0 -> {
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
                1 -> {
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
            }
        }
    }
}
