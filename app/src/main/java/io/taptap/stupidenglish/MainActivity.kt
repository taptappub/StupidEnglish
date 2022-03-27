package io.taptap.stupidenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import dagger.hilt.android.AndroidEntryPoint
import io.taptap.stupidenglish.NavigationKeys.Route.SENTENCES
import io.taptap.stupidenglish.features.addsentence.navigation.AddSentenceArgumentsMapper
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceContract
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceScreen
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceViewModel
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
import io.taptap.stupidenglish.features.addword.ui.AddWordScreen
import io.taptap.stupidenglish.features.addword.ui.AddWordViewModel
import io.taptap.stupidenglish.features.alarm.ui.AlarmScheduler
import io.taptap.stupidenglish.features.main.ui.MainContract
import io.taptap.stupidenglish.features.main.ui.MainViewModel
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract
import io.taptap.stupidenglish.features.sentences.ui.SentencesListScreen
import io.taptap.stupidenglish.features.sentences.ui.SentencesListViewModel
import io.taptap.stupidenglish.features.stack.ui.StackContract
import io.taptap.stupidenglish.features.stack.ui.StackScreen
import io.taptap.stupidenglish.features.stack.ui.StackViewModel
import io.taptap.stupidenglish.features.words.ui.WordListContract
import io.taptap.stupidenglish.features.words.ui.WordListScreen
import io.taptap.stupidenglish.features.words.ui.WordListViewModel
import io.taptap.stupidenglish.ui.StupidEnglishBottomBar
import io.taptap.stupidenglish.ui.StupidEnglishScaffold
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

const val URI = "https://stupidenglish.app"

@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalMaterialNavigationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmScheduler.enableNotifications()
        alarmScheduler.schedulePushNotifications()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            StupidEnglishTheme {
                StupidApp()
            }
        }
    }

    @ExperimentalMaterialApi
    @OptIn(InternalCoroutinesApi::class)
    @ExperimentalAnimationApi
    @ExperimentalMaterialNavigationApi
    @Composable
    private fun StupidApp() {
        val navController = rememberAnimatedNavController()
        val scaffoldState = rememberScaffoldState()

        val mainViewModel: MainViewModel = hiltViewModel()
        val mainState = mainViewModel.viewState.value

        if (mainState.isShownGreetings) {
            AlertDialog(
                onDismissRequest = {
                    mainViewModel.setEvent(MainContract.Event.OnGreetingsClose)
                },
                text = {
                    Text(text = stringResource(id = R.string.main_dialog_message))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            mainViewModel.setEvent(MainContract.Event.OnGreetingsClose)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.main_dialog_ok))
                    }
                }
            )
        }

        StupidEnglishScaffold(
            scaffoldState = scaffoldState,
//            bottomBar = {
//                if (mainState.shouldShowBottomBar(navController)) {
//                    StupidEnglishBottomBar(
//                        state = mainState,
//                        currentRoute = navController.currentRoute!!,
//                        effectFlow = mainViewModel.effect,
//                        onEventSent = { event -> mainViewModel.setEvent(event) },
//                        onNavigationRequested = { navigationEffect ->
//                            if (navigationEffect is MainContract.Effect.Navigation.OnTabSelected) {
//                                if (navigationEffect.route != navController.currentRoute) {
//                                    navController.navigateToTab(navigationEffect.route)
//                                }
//                            }
//                        }
//                    )
//                }
//            }
        ) { innerPaddingModifier ->
            AnimatedNavHost(
                navController = navController,
                startDestination = NavigationKeys.Route.SE_MAIN,
                modifier = Modifier.padding(innerPaddingModifier)
            ) {
                navigation(
                    startDestination = NavigationKeys.BottomNavigationScreen.SE_WORDS.route,
                    route = NavigationKeys.Route.SE_MAIN
                ) {
                    composable(
                        route = NavigationKeys.BottomNavigationScreen.SE_WORDS.route,
                        enterTransition = null
                    ) {
                        WordListDestination(
                            navController = navController,
                            onEventSent = { event -> mainViewModel.setEvent(event) }
                        )
                    }
                    composable(
                        route = NavigationKeys.BottomNavigationScreen.SE_SENTENCES.route,
                        arguments = listOf(navArgument(NavigationKeys.Arg.WORDS_ID) {
                            nullable = true
                            defaultValue = null
                        }),
                        enterTransition = null
                    ) {
                        SentenceListDestination(navController = navController)
                    }
                }
                composable(
                    route = NavigationKeys.Route.SE_ADD_WORD,
                    enterTransition = {
                        fadeIn()
//                        slideInVertically(initialOffsetY = { 1000 })
                    },
                    exitTransition = {
                        fadeOut()
//                        slideOutVertically(targetOffsetY = { 1000 })
                    }
                ) {
                    AddWordDialogDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_REMEMBER,
                    deepLinks = listOf(navDeepLink {
                        uriPattern =
                            "$URI/${NavigationKeys.Arg.WORDS_ID}={${NavigationKeys.Arg.WORDS_ID}}"
                    }),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) {
                    StackDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_ADD_SENTENCE,
                    deepLinks = listOf(navDeepLink {
                        uriPattern =
                            "$URI/${NavigationKeys.Arg.SENTENCE_WORDS_ID}={${NavigationKeys.Arg.SENTENCE_WORDS_ID}}"
                    }),
                    enterTransition = {
                        fadeIn()
//                        slideInVertically(initialOffsetY = { 1000 })
                    },
                    exitTransition = {
                        fadeOut()
//                        slideOutVertically(targetOffsetY = { 1000 })
                    }
                ) {
                    AddSentenceDialogDestination(navController)
                }
            }

            if (mainState.shouldShowBottomBar(navController)) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val (bottomBar) = createRefs()
                    StupidEnglishBottomBar(
                        state = mainState,
                        currentRoute = navController.currentRoute!!,
                        effectFlow = mainViewModel.effect,
                        onEventSent = { event -> mainViewModel.setEvent(event) },
                        onNavigationRequested = { navigationEffect ->
                            if (navigationEffect is MainContract.Effect.Navigation.OnTabSelected) {
                                if (navigationEffect.route != navController.currentRoute) {
                                    navController.navigateToTab(navigationEffect.route)
                                }
                            }
                        },
                        modifier = Modifier.constrainAs(bottomBar) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )
                }
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
                navController.popBackStack()
            }
        })
}

@Composable
private fun StackDestination(navController: NavHostController) {
    val stackViewModel: StackViewModel = hiltViewModel()
    val stackState = stackViewModel.viewState.value

    StackScreen(
        context = LocalContext.current,
        state = stackState,
        effectFlow = stackViewModel.effect,
        onEventSent = { event -> stackViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is StackContract.Effect.Navigation.BackToSentenceList -> {
                    navController.popBackStack()
                }
                is StackContract.Effect.Navigation.ToAddSentence -> {
                    val ids = AddSentenceArgumentsMapper.mapTo(navigationEffect.wordIds)
                    navController.navigate("${NavigationKeys.Route.ADD_SENTENCE}/${ids}") {
                        popUpTo(route = NavigationKeys.BottomNavigationScreen.SE_SENTENCES.route)
                    }
                }
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
//                navController.navigate(route = SENTENCES) {
//                    popUpTo(findStartDestination(navController.graph).id) {
//                        saveState = true
//                    }
//                }
                navController.backQueue.removeIf {
                    it.destination.route == NavigationKeys.Route.SE_ADD_SENTENCE
                            || it.destination.route == NavigationKeys.Route.SE_REMEMBER
                }
                navController.navigateToTab(route = SENTENCES) {
                    popUpTo(route = NavigationKeys.Route.SE_ADD_SENTENCE) {
                        inclusive = true
                    }
//                    popUpTo(route = NavigationKeys.BottomNavigationScreen.SE_SENTENCES.route)
                }
//                navController.popBackStack(
//                    route = NavigationKeys.BottomNavigationScreen.SE_SENTENCES.route,
//                    inclusive = false
//                )
            }
        })
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun WordListDestination(
    navController: NavHostController,
    onEventSent: (event: MainContract.Event) -> Unit
) {
    val wordViewModel: WordListViewModel = hiltViewModel()
    val wordState = wordViewModel.viewState.value

    WordListScreen(
        context = LocalContext.current,
        state = wordState,
        effectFlow = wordViewModel.effect,
        onEventSent = { event -> wordViewModel.setEvent(event) },
        onChangeBottomSheetVisibility = { visibility ->
            onEventSent(MainContract.Event.ChangeBottomSheetVisibility(visibility))
        },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is WordListContract.Effect.Navigation.ToAddWord -> {
                    navController.navigate(NavigationKeys.Route.SE_ADD_WORD)
                }
                is WordListContract.Effect.Navigation.ToAddSentence -> {
                    val ids = AddSentenceArgumentsMapper.mapTo(navigationEffect.wordIds)
                    navController.navigateToTab(
                        route = "$SENTENCES?${NavigationKeys.Arg.WORDS_ID}=$ids"
                    )
                }
            }
        })
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
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
                val ids = AddSentenceArgumentsMapper.mapTo(navigationEffect.wordIds)
                navController.navigate("${NavigationKeys.Route.REMEMBER}/${ids}")
            }
        })
}

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}

private val NavHostController.currentRoute: String?
    get() = currentDestination?.route


@Composable
private fun MainContract.State.shouldShowBottomBar(navController: NavHostController): Boolean {
    val tabs = bottomBarTabs.map { it.route }
    val curRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    return tabs.containsRoute(curRoute) && isBottomBarShown
}

private fun List<String>.containsRoute(curRoute: String?): Boolean {
    forEach {
        if (curRoute?.contains(it) == true) {
            return true
        }
    }
    return false
}

private fun NavController.navigateToTab(
    route: String,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        // Pop up backstack to the first destination and save state. This makes going back
        // to the start destination when pressing back in any other bottom tab.
        popUpTo(findStartDestination(graph).id) {
            saveState = true
        }
        builder?.let { this.it() }
    }
}

//todo
//3) Добавление одинаковых слов
//5) Сохранения слова без подсказки
//6) Добавление картинки, как подсказки
//7) Редактирование
//8) импорт
//9) ОНБОРДИНГ (+состояния пустых списков)
//10) A/b тестирование


//Следующий билд
//12) палочка в bottomSheet
//13) редизайн
//14) Дизайн система! - найди одинаковые виджеты и вынеси как один объект
//1) Обложить все аналитикой, чтобы смотреть, куда нажимает пользователь (1) Катя не поняла, что внизу табы, 2) нажимала на слово, чтобы сделать предложение, 3) нажимала на слова в ADD_SENTENCE
//2) Поменять иконку
//11) верхняя навигация

//Гугл аналитика без play service'ов
//https://developers.google.com/analytics/devguides/collection/android/v4?hl=ru

//FIXME БАГИ
// - если кучу раз нажать на слово, то блокируется UI
// - по клику на банне рне всегда начинается сценарий добавления предложения
// - при удалении, при лонг тапе, карточка слова поднимается и там и остается
// - снек без отступа снизу