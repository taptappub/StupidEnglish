package io.taptap.stupidenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import io.taptap.stupidenglish.features.details.ui.StupidWordScreen
import io.taptap.stupidenglish.features.details.ui.StupidWordViewModel
import io.taptap.stupidenglish.features.main.ui.MainListContract
import io.taptap.stupidenglish.features.main.ui.MainListScreen
import io.taptap.stupidenglish.features.main.ui.MainListViewModel
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme

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

@Composable
private fun MainListDestination(navController: NavHostController) {
    val viewModel: MainListViewModel = hiltViewModel()
    val state = viewModel.viewState.value
    MainListScreen(
        state = state,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is MainListContract.Effect.Navigation.ToCategoryDetails) {
                navController.navigate("${NavigationKeys.Route.SE_LIST}/${navigationEffect.categoryName}")
            }
        })
}

@Composable
private fun StupidWordDestination() {
    val viewModel: StupidWordViewModel = hiltViewModel()
    val state = viewModel.viewState.value
    StupidWordScreen(state)
}
