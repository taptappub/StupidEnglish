package io.taptap.stupidenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import io.taptap.stupidenglish.features.details.ui.StupidWordScreen
import io.taptap.stupidenglish.features.details.ui.StupidWordViewModel
import io.taptap.stupidenglish.features.main.ui.MainListContract
import io.taptap.stupidenglish.features.main.ui.MainListScreen
import io.taptap.stupidenglish.features.main.ui.MainListViewModel
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract
import io.taptap.stupidenglish.features.sentences.ui.SentencesListScreen
import io.taptap.stupidenglish.features.sentences.ui.SentencesListViewModel
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StupidEnglishTheme {
                StupidApp()
            }
        }
    }

    @ExperimentalPagerApi
    @Composable
    private fun StupidApp() {
        val navController = rememberNavController()
        NavHost(navController, startDestination = NavigationKeys.Route.SE_LIST) {
            composable(route = NavigationKeys.Route.SE_LIST) {
                MainListDestination(navController)
            }
            composable(
                route = NavigationKeys.Route.SE_WORD_DETAILS,
                arguments = listOf(navArgument(NavigationKeys.Arg.WORD_ID) {
                    type = NavType.StringType
                })
            ) {
                StupidWordDestination()
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun MainListDestination(navController: NavHostController) {
    Box {
        val pagerState = rememberPagerState()

        HorizontalPager(
            count = 2,
            state = pagerState
        ) { page ->
            when (page) {
                0 -> {
                    val wordViewModel: MainListViewModel = hiltViewModel()
                    val wordState = wordViewModel.viewState.value

                    MainListScreen(
                        state = wordState,
                        effectFlow = wordViewModel.effect,
                        onEventSent = { event -> wordViewModel.setEvent(event) },
                        onNavigationRequested = { navigationEffect ->
                            if (navigationEffect is MainListContract.Effect.Navigation.ToCategoryDetails) {
                                navController.navigate("${NavigationKeys.Route.SE_LIST}/${navigationEffect.categoryName}")
                            }
                        })
                }
                1 -> {
                    val sentenceViewModel: SentencesListViewModel = hiltViewModel()
                    val sentenceState = sentenceViewModel.viewState.value

                    SentencesListScreen(
                        state = sentenceState,
                        effectFlow = sentenceViewModel.effect,
                        onEventSent = { event -> sentenceViewModel.setEvent(event) },
                        onNavigationRequested = { navigationEffect ->
                            if (navigationEffect is SentencesListContract.Effect.Navigation.ToCategoryDetails) {
                                navController.navigate("${NavigationKeys.Route.SE_LIST}/${navigationEffect.categoryName}")
                            }
                        })
                }

            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
        )
    }
}

@Composable
private fun StupidWordDestination() {
    val viewModel: StupidWordViewModel = hiltViewModel()
    val state = viewModel.viewState.value
    StupidWordScreen(state)
}
