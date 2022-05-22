package io.taptap.stupidenglish.features.stack.ui

import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.DiffUtil
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import com.yuyakaido.android.cardstackview.SwipeableMethod
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceContract
import io.taptap.stupidenglish.features.stack.ui.adapter.CardStackAdapter
import io.taptap.stupidenglish.features.stack.ui.adapter.CardStackDiffUtils
import io.taptap.stupidenglish.features.stack.ui.adapter.randomDirection
import io.taptap.uikit.SecondaryButton
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.StupidEnglishTopAppBar
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun StackScreen(
    context: Context,
    state: StackContract.State,
    effectFlow: Flow<StackContract.Effect>?,
    onEventSent: (event: StackContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: StackContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    //todo manager и adapter объединить в recyclerState по типу scafoldState
    val manager: CardStackLayoutManager = remember {
        initCardStackLayoutManager(context, onEventSent).init()
    }
    val adapter: CardStackAdapter = remember {
        CardStackAdapter(
            words = state.words,
            onEventSent = onEventSent
        )
    }

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is StackContract.Effect.GetWordsError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(effect.errorRes),
                        duration = SnackbarDuration.Short
                    )
                is StackContract.Effect.SaveError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(effect.errorRes),
                        duration = SnackbarDuration.Short
                    )
                is StackContract.Effect.Navigation.BackToSentenceList -> onNavigationRequested(
                    effect
                )
                is StackContract.Effect.Navigation.ToAddSentence -> onNavigationRequested(
                    effect
                )
            }
        }?.collect()
    }

    StupidEnglishScaffold(
        scaffoldState = scaffoldState,
    ) {
        ContentScreen(
            state = state,
            manager = manager,
            adapter = adapter,
            onEventSent = onEventSent
        )
    }
}

@Composable
private fun ContentScreen(
    state: StackContract.State,
    manager: CardStackLayoutManager,
    adapter: CardStackAdapter,
    onEventSent: (event: StackContract.Event) -> Unit
) {
    StupidLanguageBackgroundBox(
        topbar = {
            StupidEnglishTopAppBar(
                text = stringResource(id = R.string.stck_topbar_title),
                onNavigationClick = { onEventSent(StackContract.Event.OnBackClick) },
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            AndroidView(
                factory = { context ->
                    CardStackView(context)
                        .apply {
                            layoutManager = manager
                            setAdapter(adapter)
                        }
                },
                update = { view ->
                    val cardStackDiffUtils = CardStackDiffUtils(adapter.words, state.words)
                    val diffResult = DiffUtil.calculateDiff(cardStackDiffUtils);
                    adapter.words = state.words
                    diffResult.dispatchUpdatesTo(adapter)

                    if (state.swipeState is StackContract.SwipeState.WasSwiped) {
                        val setting = SwipeAnimationSetting.Builder()
                            .randomDirection()
                            .setDuration(Duration.Normal.duration)
                            .setInterpolator(AccelerateInterpolator())
                            .build()
                        manager.setSwipeAnimationSetting(setting)
                        view.swipe()
                        onEventSent(StackContract.Event.EndSwipe)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 36.dp)
                    .weight(1.0f, false)
            )

            SecondaryButton(
                text = stringResource(id = R.string.stck_button_remember),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(vertical = 16.dp)
            ) {
                onEventSent(StackContract.Event.Swipe)
            }
        }
    }
}

private fun initCardStackLayoutManager(
    context: Context,
    onEventSent: (event: StackContract.Event) -> Unit
): CardStackLayoutManager {
    return CardStackLayoutManager(context, object : CardStackListener {
        override fun onCardDragging(direction: Direction, ratio: Float) {
            when (direction) {
                Direction.Left -> onEventSent(StackContract.Event.OnYes)
                Direction.Right -> onEventSent(StackContract.Event.OnYes)
                Direction.Top -> onEventSent(StackContract.Event.OnYes)
                Direction.Bottom -> onEventSent(StackContract.Event.OnYes)
            }
        }

        override fun onCardSwiped(direction: Direction) {
            when (direction) {
                Direction.Left -> onEventSent(StackContract.Event.OnYes)
                Direction.Right -> onEventSent(StackContract.Event.OnYes)
                Direction.Top -> onEventSent(StackContract.Event.OnYes)
                Direction.Bottom -> onEventSent(StackContract.Event.OnYes)
            }
        }

        override fun onCardRewound() {
        }

        override fun onCardCanceled() {
        }

        override fun onCardAppeared(view: View?, position: Int) {
            onEventSent(StackContract.Event.OnCardAppeared(position))
        }

        override fun onCardDisappeared(view: View?, position: Int) {
            onEventSent(StackContract.Event.OnCardDisappeared(position))
        }

    })
}

private fun CardStackLayoutManager.init(): CardStackLayoutManager {
    return apply {
        setStackFrom(StackFrom.Right)
        setTranslationInterval(16.0f)
        setScaleInterval(0.8f)
        setSwipeThreshold(0.3f)
        setVisibleCount(3)
        setMaxDegree(20.0f)
        setDirections(Direction.FREEDOM)
        setCanScrollHorizontal(true)
        setCanScrollVertical(true)
        setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        setOverlayInterpolator(LinearInterpolator())
    }
}
