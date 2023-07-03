package io.taptap.stupidenglish.features.splash.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun SplashScreen(
    state: SplashContract.State,
    effectFlow: Flow<SplashContract.Effect>?,
    onEventSent: (event: SplashContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: SplashContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is SplashContract.Effect.Navigation.ToAuthScreen ->
                    onNavigationRequested(effect)

                is SplashContract.Effect.Navigation.ToWordListScreen ->
                    onNavigationRequested(effect)
            }
        }?.collect()
    }

    StupidEnglishScaffold(
        scaffoldState = scaffoldState
    ) {
        StupidLanguageBackgroundBox {
            ContentScreen(
                state,
                onEventSent
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun ContentScreen(
    state: SplashContract.State,
    onEventSent: (event: SplashContract.Event) -> Unit
) {
    var languages by remember { mutableIntStateOf(0) }
    val languageCounter by animateIntAsState(
        targetValue = languages,
        animationSpec = tween(
            durationMillis = state.startAnimationDuration.toInt(),
            delayMillis = state.startAnimationDelay.toInt(),
            easing = CubicBezierEasing(0.2f, 0.0f, 0.2f, 1.0f)
        ),
        finishedListener = {
            onEventSent(SplashContract.Event.OnAnimationEnd)
        }
    )
    LaunchedEffect(Unit) {
        languages = state.list.size - 1
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Text(
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 42.sp,
                lineHeight = 42.sp,
                text = "Stupid "
            )

            Box {
                Text(
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Transparent,
                    fontSize = 42.sp,
                    lineHeight = 42.sp,
                    text = "Language"
                )

                AnimatedContent(
                    targetState = languageCounter,
                    transitionSpec = {
                        (slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut())
                            .using(
                                // Disable clipping since the faded slide-in/out should
                                // be displayed out of bounds.
                                SizeTransform(clip = false)
                            )
                    },
                ) { targetCount ->
                    Text(
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 42.sp,
                        lineHeight = 42.sp,
                        text = state.list[targetCount]
                    )
                }
            }
        }
    }
}
