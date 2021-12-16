package io.taptap.stupidenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import io.taptap.stupidenglish.features.addword.ui.AddWordViewModel
import io.taptap.stupidenglish.features.details.ui.StupidWordScreen
import io.taptap.stupidenglish.features.details.ui.StupidWordViewModel
import io.taptap.stupidenglish.features.main.ui.MainListContract
import io.taptap.stupidenglish.features.main.ui.MainListScreen
import io.taptap.stupidenglish.features.main.ui.MainListViewModel
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract
import io.taptap.stupidenglish.features.sentences.ui.SentencesListScreen
import io.taptap.stupidenglish.features.sentences.ui.SentencesListViewModel
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import io.taptap.stupidenglish.ui.theme.getIndicatorActiveColor
import io.taptap.stupidenglish.ui.theme.getIndicatorInactiveColor

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
        val navController = rememberNavController(bottomSheetNavigator)
        ModalBottomSheetLayout(bottomSheetNavigator) {
            NavHost(navController, startDestination = NavigationKeys.Route.SE_LIST) {
                composable(route = NavigationKeys.Route.SE_LIST) {
                    MainListDestination(navController)
                }
//                composable(
//                    route = NavigationKeys.Route.SE_WORD_DETAILS,
//                    arguments = listOf(navArgument(NavigationKeys.Arg.WORD_ID) {
//                        type = NavType.StringType
//                    })
//                ) {
//                    StupidWordDestination()
//                }
                bottomSheet(route = NavigationKeys.Route.SE_ADD_WORD) {
                    AddWordDialogDestination(navController)
                }
            }
        }
    }
}

@Composable
private fun AddWordDialogDestination(navController: NavHostController) {
//    val addWordViewModel: AddWordViewModel = hiltViewModel()
//    val addWordState = addWordViewModel.viewState.value

    Text(text = "Hello there!")
//    AddWordScreen(
//        state = addWordState,
//        effectFlow = addWordViewModel.effect,
//        onEventSent = { event -> addWordViewModel.setEvent(event) },
//        onNavigationRequested = { navigationEffect ->
//            if (navigationEffect is AddWordContract.Effect.Navigation.ToCategoryDetails) {
//                navController.navigate("${NavigationKeys.Route.SE_LIST}/${navigationEffect.categoryName}")
//            }
//        })
}

@ExperimentalPagerApi
@Composable
private fun MainListDestination(navController: NavHostController) {
    Column {
        val pagerState = rememberPagerState()
        StupidEnglishTheme {
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
        }
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
                            if (navigationEffect is MainListContract.Effect.Navigation.ToAddWord) {
                                navController.navigate(NavigationKeys.Route.SE_ADD_WORD)
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
    }
}

@Composable
private fun StupidWordDestination() {
    val viewModel: StupidWordViewModel = hiltViewModel()
    val state = viewModel.viewState.value
    StupidWordScreen(state)
}
