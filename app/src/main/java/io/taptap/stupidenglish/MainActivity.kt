package io.taptap.stupidenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import io.taptap.stupidenglish.features.auth.ui.AuthContract
import io.taptap.stupidenglish.features.auth.ui.AuthScreen
import io.taptap.stupidenglish.features.auth.ui.AuthViewModel
import io.taptap.stupidenglish.features.importwords.ui.ImportWordsContract
import io.taptap.stupidenglish.features.importwords.ui.ImportWordsScreen
import io.taptap.stupidenglish.features.importwords.ui.ImportWordsViewModel
import io.taptap.stupidenglish.features.importwordstutorial.ui.ImportWordsTutorialContract
import io.taptap.stupidenglish.features.importwordstutorial.ui.ImportWordsTutorialScreen
import io.taptap.stupidenglish.features.importwordstutorial.ui.ImportWordsTutorialViewModel
import io.taptap.stupidenglish.features.main.ui.MainContract
import io.taptap.stupidenglish.features.main.ui.MainViewModel
import io.taptap.stupidenglish.features.profile.ui.ProfileContract
import io.taptap.stupidenglish.features.profile.ui.ProfileScreen
import io.taptap.stupidenglish.features.profile.ui.ProfileViewModel
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract
import io.taptap.stupidenglish.features.sentences.ui.SentencesListScreen
import io.taptap.stupidenglish.features.sentences.ui.SentencesListViewModel
import io.taptap.stupidenglish.features.splash.ui.SplashContract
import io.taptap.stupidenglish.features.splash.ui.SplashScreen
import io.taptap.stupidenglish.features.splash.ui.SplashViewModel
import io.taptap.stupidenglish.features.stack.ui.StackContract
import io.taptap.stupidenglish.features.stack.ui.StackScreen
import io.taptap.stupidenglish.features.stack.ui.StackViewModel
import io.taptap.stupidenglish.features.termsandconditions.ui.TermsContract
import io.taptap.stupidenglish.features.termsandconditions.ui.TermsScreen
import io.taptap.stupidenglish.features.termsandconditions.ui.TermsViewModel
import io.taptap.stupidenglish.features.words.ui.WordListContract
import io.taptap.stupidenglish.features.words.ui.WordListScreen
import io.taptap.stupidenglish.features.words.ui.WordListViewModel
import io.taptap.stupidenglish.ui.StupidEnglishBottomBar
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.theme.StupidEnglishTheme
import javax.inject.Inject

const val URI = "https://stupidenglish.app"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @OptIn(
        ExperimentalMaterialApi::class,
        ExperimentalAnimationApi::class,
        ExperimentalMaterialNavigationApi::class
    )
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
    @OptIn(
        ExperimentalFoundationApi::class
    )
    @ExperimentalAnimationApi
    @ExperimentalMaterialNavigationApi
    @Composable
    private fun StupidApp() {
        val navController = rememberAnimatedNavController()
        val scaffoldState = rememberScaffoldState()

        val mainViewModel: MainViewModel = hiltViewModel()
        val mainState = mainViewModel.viewState.value

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
                startDestination = NavigationKeys.Route.SE_SPLASH,
                modifier = Modifier.padding(innerPaddingModifier)
            ) {
                composable(
                    route = NavigationKeys.Route.SE_SPLASH
                ) {
                    SplashDestination(navController)
                }
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
                        SentenceListDestination(
                            navController = navController,
                            onEventSent = { event -> mainViewModel.setEvent(event) }
                        )
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
                    AddWordDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_IMPORT_WORDS
                ) {
                    ImportWordsDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_TERMS
                ) {
                    TermsDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_IMPORT_WORDS_TUTORIAL
                ) {
                    ImportWordsTutorialDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_PROFILE
                ) {
                    ProfileDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_AUTH
                ) {
                    AuthDestination(navController)
                }
                /*composable(
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
                }*/
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
                    AddSentenceDestination(navController)
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

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
private fun SplashDestination(
    navController: NavHostController
) {
    val splashViewModel: SplashViewModel = hiltViewModel()
    val splashState = splashViewModel.viewState.value

    SplashScreen(
        context = LocalContext.current,
        state = splashState,
        effectFlow = splashViewModel.effect,
        onEventSent = { event -> splashViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is SplashContract.Effect.Navigation.ToAuthScreen -> {
                    navController.navigate(route = NavigationKeys.Route.SE_AUTH) {
                        popUpTo(route = NavigationKeys.Route.SE_SPLASH) {
                            inclusive = true
                        }
                    }
                }
                is SplashContract.Effect.Navigation.ToWordListScreen -> {
                    navController.navigate(route = NavigationKeys.Route.SE_MAIN) {
                        popUpTo(route = NavigationKeys.Route.SE_SPLASH) {
                            inclusive = true
                        }
                    }
                }
            }
        })
}

@ExperimentalMaterialApi
@Composable
private fun AddWordDestination(
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

@ExperimentalMaterialApi
@Composable
private fun ImportWordsDestination(
    navController: NavHostController
) {
    val importWordsViewModel: ImportWordsViewModel = hiltViewModel()
    val importWordsState = importWordsViewModel.viewState.value

    ImportWordsScreen(
        context = LocalContext.current,
        state = importWordsState,
        effectFlow = importWordsViewModel.effect,
        onEventSent = { event -> importWordsViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is ImportWordsContract.Effect.Navigation.BackToWordList -> {
                    navController.popBackStack()
                }
                is ImportWordsContract.Effect.Navigation.GoToImportTutorial -> {
                    navController.navigate(NavigationKeys.Route.SE_IMPORT_WORDS_TUTORIAL)
                }
            }
        })
}

@ExperimentalMaterialApi
@Composable
private fun TermsDestination(
    navController: NavHostController
) {
    val termsViewModel: TermsViewModel = hiltViewModel()
    val termsState = termsViewModel.viewState.value

    TermsScreen(
        context = LocalContext.current,
        state = termsState,
        effectFlow = termsViewModel.effect,
        onEventSent = { event -> termsViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is TermsContract.Effect.Navigation.BackToProfile -> {
                    navController.popBackStack()
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun ImportWordsTutorialDestination(
    navController: NavHostController
) {
    val importWordsTutorialViewModel: ImportWordsTutorialViewModel = hiltViewModel()
    val importWordsTutorialState = importWordsTutorialViewModel.viewState.value

    ImportWordsTutorialScreen(
        context = LocalContext.current,
        state = importWordsTutorialState,
        effectFlow = importWordsTutorialViewModel.effect,
        onEventSent = { event -> importWordsTutorialViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is ImportWordsTutorialContract.Effect.Navigation.BackToImportWords) {
                navController.popBackStack()
            }
        })
}

@ExperimentalMaterialApi
@Composable
private fun ProfileDestination(
    navController: NavHostController
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileState = profileViewModel.viewState.value

    ProfileScreen(
        context = LocalContext.current,
        state = profileState,
        effectFlow = profileViewModel.effect,
        onEventSent = { event -> profileViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is ProfileContract.Effect.Navigation.BackToWordsList -> {
                    navController.popBackStack()
                }
                is ProfileContract.Effect.Navigation.GoToTermsAndConditions -> {
                    navController.navigate(NavigationKeys.Route.SE_TERMS)
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun AuthDestination(
    navController: NavHostController
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState = authViewModel.viewState.value

    AuthScreen(
        context = LocalContext.current,
        state = authState,
        effectFlow = authViewModel.effect,
        onEventSent = { event -> authViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is AuthContract.Effect.Navigation.ToWordsList) {
                navController.navigate(route = NavigationKeys.Route.SE_MAIN) {
                    popUpTo(route = NavigationKeys.Route.SE_SPLASH)
                }
            }
        }
    )
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
private fun AddSentenceDestination(navController: NavHostController) {
    val addSentenceViewModel: AddSentenceViewModel = hiltViewModel()
    val addSentenceState = addSentenceViewModel.viewState.value

    AddSentenceScreen(
        context = LocalContext.current,
        state = addSentenceState,
        effectFlow = addSentenceViewModel.effect,
        onEventSent = { event -> addSentenceViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is AddSentenceContract.Effect.Navigation.BackToSentenceList) {
                navController.backQueue.removeIf {
                    it.destination.route == NavigationKeys.Route.SE_ADD_SENTENCE
                            //|| it.destination.route == NavigationKeys.Route.SE_REMEMBER
                }
                navController.navigateToTab(route = SENTENCES) {
                    popUpTo(route = NavigationKeys.Route.SE_ADD_SENTENCE) {
                        inclusive = true
                    }
                }
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
                is WordListContract.Effect.Navigation.ToImportWords -> {
                    navController.navigate(NavigationKeys.Route.SE_IMPORT_WORDS)
                }
                is WordListContract.Effect.Navigation.ToProfile -> {
                    navController.navigate(NavigationKeys.Route.SE_PROFILE)
                }
            }
        })
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun SentenceListDestination(
    navController: NavHostController,
    onEventSent: (event: MainContract.Event) -> Unit
) {
    val sentenceViewModel: SentencesListViewModel = hiltViewModel()
    val sentenceState = sentenceViewModel.viewState.value

    SentencesListScreen(
        context = LocalContext.current,
        state = sentenceState,
        effectFlow = sentenceViewModel.effect,
        onEventSent = { event -> sentenceViewModel.setEvent(event) },
        onChangeBottomSheetVisibility = { visibility ->
            onEventSent(MainContract.Event.ChangeBottomSheetVisibility(visibility))
        },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is SentencesListContract.Effect.Navigation.ToAddSentence) {
                val ids = AddSentenceArgumentsMapper.mapTo(navigationEffect.wordIds)
                navController.navigate("${NavigationKeys.Route.ADD_SENTENCE}/${ids}")
//                navController.navigate("${NavigationKeys.Route.REMEMBER}/${ids}")
            }
        }
    )
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
    try {
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
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

//todo
//3) Добавление одинаковых слов
//5) Сохранения слова без подсказки
//6) Добавление картинки, как подсказки
//7) Редактирование
//9) ОНБОРДИНГ (+состояния пустых списков)
//10) A/b тестирование
//11) Перетаскивание в папку слов драг энд дропом. Список групп вылезает сбоку, с анимацией волны, и ты перетягиваешь слово в нужную папку
//12) импорт из Quizlet по ссылке модуля
//13) Можно создавать имя группы при импорте из названия страницы
//4) механизм переключения темы (темная, светлая или системная)
//5) Обучение карточками
//6) Сделать магазин с карточками, чтобы можно было себе оттуда добавлять уже готовые наборы


//Гугл аналитика без play service'ов
//https://developers.google.com/analytics/devguides/collection/android/v4?hl=ru

//FIXME БАГИ
// - если кучу раз нажать на слово, то блокируется UI
// - если из импорта перейти на боттмошит для добавления группы, то диалог открывается, но фокус на него не ставится и клавиатура не поднимается
// - в gmail не подставляется subject при отправке письма из настроек
// - Если на этом этапе нажать вне попапа, то происходит попытка отправки репорта с ошибкой


//Следующий билд
//2) https://stackoverflow.com/questions/64362801/how-to-handle-visibility-of-a-text-in-jetpack-compose ДЛЯ экрана импорта
//1) Обложить все аналитикой, чтобы смотреть, куда нажимает пользователь (1) Катя не поняла, что внизу табы, 2) нажимала на слово, чтобы сделать предложение, 3) нажимала на слова в ADD_SENTENCE
//2) Авторизация и сохранение слов в firebase storage
//4) “Новое слово” - подсказка, буквы не регистрируются при вводе
//5) Диалог подтверждения удаления группы
//6) Статистика по изучению
//11) При удалении слова есть возможность его восстановить какое-то время, а при удалении группы такой возможности нет. Может тоже стоит поповер выводить, а то если нечаянно удалишь всю группу с кучей слов, будет очень обидно
