package io.taptap.stupidenglish.features.stack.ui

import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView
import com.yuyakaido.android.cardstackview.*
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.features.stack.ui.adapter.CardStackAdapter
import io.taptap.stupidenglish.ui.theme.getSuccessTextColor
import io.taptap.stupidenglish.ui.theme.getTitleTextColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import com.yuyakaido.android.cardstackview.Direction

@Composable
fun StackScreen(
    context: Context,
    state: StackContract.State,
    effectFlow: Flow<StackContract.Effect>?,
    onEventSent: (event: StackContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: StackContract.Effect.Navigation) -> Unit
) {
    Box {
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        val manager: CardStackLayoutManager =
            initCardStackLayoutManager(context, state, onEventSent)

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

        Scaffold(
            scaffoldState = scaffoldState,
        ) {
            ContentScreen(
                state = state,
                manager = manager,
                onEventSent = onEventSent
            )
        }
    }
}

@Composable
private fun ContentScreen(
    state: StackContract.State,
    manager: CardStackLayoutManager,
    onEventSent: (event: StackContract.Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        AndroidView(
            factory = { context ->
                CardStackView(context)
                    .apply { layoutManager = manager.init() }
            },
            update = { view ->
                view.adapter = CardStackAdapter(state.words)
                if (state.swipeState is StackContract.SwipeState.WasSwiped) {
                    val setting = SwipeAnimationSetting.Builder()
                        .setDirection(state.swipeState.direction)
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

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, start = 48.dp, end = 48.dp, top = 24.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 100.dp,
                modifier = Modifier.clickable {
                    onEventSent(StackContract.Event.Swipe(Direction.Left))
                }
            ) {
                Text(
                    text = stringResource(id = R.string.stck_button_no),
                    fontSize = 24.sp,
                    color = getTitleTextColor(),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 16.dp,
                        start = 48.dp,
                        end = 48.dp
                    )
                )
            }
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 100.dp,
                modifier = Modifier.clickable {
                    onEventSent(StackContract.Event.Swipe(Direction.Right))
                }
            ) {
                Text(
                    text = stringResource(id = R.string.stck_button_yes),
                    fontSize = 24.sp,
                    color = getSuccessTextColor(),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 16.dp,
                        start = 48.dp,
                        end = 48.dp
                    )
                )
            }
        }
    }
}

fun initCardStackLayoutManager(
    context: Context,
    state: StackContract.State,
    onEventSent: (event: StackContract.Event) -> Unit
): CardStackLayoutManager {
    return CardStackLayoutManager(context, object : CardStackListener {
        override fun onCardDragging(direction: Direction, ratio: Float) {
            when (direction) {
                Direction.Left -> onEventSent(StackContract.Event.OnNo)
                Direction.Right -> onEventSent(StackContract.Event.OnYes)
                Direction.Top -> { /*do nothing*/
                }
                Direction.Bottom -> { /*do nothing*/
                }
            }
        }

        override fun onCardSwiped(direction: Direction) {
            when (direction) {
                Direction.Left -> onEventSent(StackContract.Event.OnNo)
                Direction.Right -> onEventSent(StackContract.Event.OnYes)
                Direction.Top -> { /*do nothing*/
                }
                Direction.Bottom -> { /*do nothing*/
                }
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

private fun CardStackLayoutManager.init(): RecyclerView.LayoutManager {
    return apply {
        setStackFrom(StackFrom.None)
        setTranslationInterval(8.0f)
        setScaleInterval(0.95f)
        setSwipeThreshold(0.3f)
        setVisibleCount(3)
        setMaxDegree(20.0f)
        setDirections(Direction.HORIZONTAL)
        setCanScrollHorizontal(true)
        setCanScrollVertical(true)
        setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        setOverlayInterpolator(LinearInterpolator())
    }
}
