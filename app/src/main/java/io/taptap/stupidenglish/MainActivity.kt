package io.taptap.stupidenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
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
            bottomBar = {
                if (mainState.shouldShowBottomBar(navController)) {
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
                        }
                    )
                }
            }
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
                        slideInVertically(initialOffsetY = { 1000 })
                    },
                    exitTransition = {
                        slideOutVertically(targetOffsetY = { 1000 })
                    }
                ) {
                    AddWordDialogDestination(navController)
                }
                composable(
                    route = "${NavigationKeys.Route.SE_REMEMBER}/{${NavigationKeys.Arg.WORDS_ID}}",
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
                    route = "${NavigationKeys.Route.SE_ADD_SENTENCE}/{${NavigationKeys.Arg.SENTENCE_WORDS_ID}}",
                    deepLinks = listOf(navDeepLink {
                        uriPattern =
                            "$URI/${NavigationKeys.Arg.SENTENCE_WORDS_ID}={${NavigationKeys.Arg.SENTENCE_WORDS_ID}}"
                    }),
                    enterTransition = {
                        slideInVertically(initialOffsetY = { 1000 })
                    },
                    exitTransition = {
                        slideOutVertically(targetOffsetY = { 1000 })
                    }
                ) {
                    AddSentenceDialogDestination(navController)
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
                    navController.navigate("${NavigationKeys.Route.SE_ADD_SENTENCE}/${ids}") {
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
                navController.popBackStack(
                    route = NavigationKeys.BottomNavigationScreen.SE_SENTENCES.route,
                    inclusive = false
                )
            }
        })
}

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
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is WordListContract.Effect.Navigation.ToAddWord -> {
                    navController.navigate(NavigationKeys.Route.SE_ADD_WORD)
                }
                is WordListContract.Effect.Navigation.ToAddSentence -> {
                    val ids = AddSentenceArgumentsMapper.mapTo(navigationEffect.wordIds)
                    navController.navigateToTab(
                        route = "$SENTENCES?${NavigationKeys.Arg.WORDS_ID}=$ids"
                    ) {
                        popUpTo(route = NavigationKeys.BottomNavigationScreen.SE_WORDS.route)
                    }
                }
            }
        })
}

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
                navController.navigate("${NavigationKeys.Route.SE_REMEMBER}/${ids}")
            }
        })
}

//todo
//4. Поменять иконку
//3) Добавление одинаковых слов
//4) Падение при добавлении предложения
//5) Сохранения слова без подсказки
//6) Добавление картинки, как подсказки
//7) Редактирование и удаление
//8) импорт

//----- First row of tasks
//1. Возвращение на страницу с предложениями - БАГ!!!
//2. Убрать управление жестами с карточек. Если No, или Tap, то переворачивается карточка,
// а там и СЛОВО и ПОДСКАЗКА, а кнопка внизу превращается во "Вроде запомнил" - не плохо бы возвращать в пул такие карты.
//3. Белая иконка на прозрачном фоне для нотификации


//1. A/b тестирование
//5. А потом релизнуть

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

    return tabs.containsRoute(curRoute)
}

private fun List<String>.containsRoute(curRoute: String?): Boolean {
    forEach {
        if (curRoute?.contains(it) == true) {
            return true
        }
    }
    return false
}

private fun NavController.navigateToTab(route: String, builder: (NavOptionsBuilder.() -> Unit)? = null) {
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
