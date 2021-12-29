package io.taptap.stupidenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceContract
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceScreen
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceViewModel
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
import io.taptap.stupidenglish.features.addword.ui.AddWordScreen
import io.taptap.stupidenglish.features.addword.ui.AddWordViewModel
import io.taptap.stupidenglish.features.main.ui.MainScreen
import io.taptap.stupidenglish.features.main.ui.MainViewModel
import io.taptap.stupidenglish.features.sentences.navigation.SentenceNavigationNavType
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalPagerApi
@ExperimentalMaterialNavigationApi
@InternalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
        val navController = rememberAnimatedNavController()
        //AnimatedNavHost(navController, startDestination = "${NavigationKeys.Route.SE_LIST}/0") {
        AnimatedNavHost(navController, startDestination = NavigationKeys.Route.SE_LIST) {
            composable(
                route = NavigationKeys.Route.SE_LIST
                /*route = NavigationKeys.Route.SE_LIST_ARG,
                arguments = listOf(navArgument(NavigationKeys.Arg.PAGE_ID) {
                    type = NavType.StringType
                })*/
            ) {
                MainDestination(navController)
            }
            composable(route = NavigationKeys.Route.SE_ADD_WORD,
                enterTransition = {
                    slideInVertically(initialOffsetY = { 1000 })
                },
                exitTransition = {
                    slideOutVertically(targetOffsetY = { 1000 })
                }) {
                AddWordDialogDestination(navController)
            }
            composable(
                route = NavigationKeys.Route.SE_ADD_SENTENCE,
                arguments = listOf(
                    navArgument(NavigationKeys.Arg.SENTENCE_WORDS_ID) {
                        type = SentenceNavigationNavType()
                    }
                ),
                enterTransition = {
                    slideInVertically(initialOffsetY = { 1000 })
                },
                exitTransition = {
                    slideOutVertically(targetOffsetY = { 1000 })
                }
            ) {
                AddSentenceDialogDestination(navController)
            }

//            bottomSheet(route = NavigationKeys.Route.SE_ADD_WORD) {
//                AddWordDialogDestination(navController)
//            }

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
                navController.popBackStack()
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
                navController.popBackStack()
            }
        })
}

@InternalCoroutinesApi
@ExperimentalPagerApi
@Composable
private fun MainDestination(navController: NavHostController) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val mainState = mainViewModel.viewState.value

    MainScreen(
        navController = navController,
        state = mainState,
        effectFlow = mainViewModel.effect,
        onEventSent = { event -> mainViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->

        })
}
