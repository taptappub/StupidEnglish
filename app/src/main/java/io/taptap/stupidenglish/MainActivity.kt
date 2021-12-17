package io.taptap.stupidenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
import io.taptap.stupidenglish.features.addword.ui.AddWordScreen
import io.taptap.stupidenglish.features.addword.ui.AddWordViewModel
import io.taptap.stupidenglish.features.details.ui.StupidWordScreen
import io.taptap.stupidenglish.features.details.ui.StupidWordViewModel
import io.taptap.stupidenglish.features.main.ui.MainListContract
import io.taptap.stupidenglish.features.main.ui.MainListScreen
import io.taptap.stupidenglish.features.main.ui.MainListViewModel
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract
import io.taptap.stupidenglish.features.sentences.ui.SentencesListScreen
import io.taptap.stupidenglish.features.sentences.ui.SentencesListViewModel
import io.taptap.stupidenglish.ui.theme.Red100
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import io.taptap.stupidenglish.ui.theme.getIndicatorActiveColor
import io.taptap.stupidenglish.ui.theme.getIndicatorInactiveColor
import kotlinx.coroutines.launch

typealias SheetContent = @Composable ColumnScope.() -> Unit

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
        val navController = rememberNavController()
        navController.navigatorProvider += bottomSheetNavigator
        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetElevation = 10.dp,
            modifier = Modifier
                .wrapContentHeight()
        ) {
            NavHost(navController, startDestination = NavigationKeys.Route.SE_LIST) {
                composable(route = NavigationKeys.Route.SE_LIST) {
                    MainListDestination(navController)
                }
                bottomSheet(route = NavigationKeys.Route.SE_ADD_WORD) {
                    AddWordDialogDestination(navController)
                }
            }
        }
    }
}

@Composable
private fun AddWordDialogDestination(navController: NavHostController) {
    val addWordViewModel: AddWordViewModel = hiltViewModel()
    val addWordState = addWordViewModel.viewState.value

    AddWordScreen(
        context = LocalContext.current,
        state = addWordState,
        effectFlow = addWordViewModel.effect,
        onEventSent = { event -> addWordViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is AddWordContract.Effect.Navigation.BackToWordList) {
                navController.navigate(NavigationKeys.Route.SE_LIST)
            }
        })
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
