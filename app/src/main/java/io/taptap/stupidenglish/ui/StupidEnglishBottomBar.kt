package io.taptap.stupidenglish.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.features.main.ui.MainContract
import io.taptap.uikit.fab.BOTTOM_BAR_HEIGHT
import io.taptap.uikit.fab.BOTTOM_BAR_VERTICAL_PADDING
import io.taptap.uikit.theme.StupidEnglishTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

//ver2
@Composable
fun StupidEnglishBottomBar(
    tabs: List<NavigationKeys.BottomNavigationScreen>,
    selectedTab: NavigationKeys.BottomNavigationScreen,
    effectFlow: Flow<MainContract.Effect>?,
    onEventSent: (event: MainContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit,
    modifier: Modifier = Modifier,
) {

    //TODO let's recompose BottomBar instead of listening events?
    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is MainContract.Effect.Navigation.OnTabSelected -> {
                    onNavigationRequested(effect)
                }
            }
        }?.collect()
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colorScheme.surface,
        elevation = 8.dp,
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = BOTTOM_BAR_VERTICAL_PADDING)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(BOTTOM_BAR_HEIGHT)
        ) {

            tabs.forEach { item ->

                StupidEnglishBottomNavigationItem(
                    item = item,
                    selected = item == selectedTab,
                    onSelected = {
                        onEventSent(MainContract.Event.OnTabSelected(item))
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically)
                        .weight(1.0f)
                )
            }
        }
    }
}

@Composable
fun StupidEnglishBottomNavigationItem(
    item: NavigationKeys.BottomNavigationScreen,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.selectable(selected = selected, onClick = onSelected),
        horizontalArrangement = Arrangement.Center
    ) {

        val tint by animateColorAsState(
            if (selected) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )

        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = stringResource(id = item.title),
            tint = tint,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(6.dp)
        )
        Text(
            text = stringResource(id = item.title),
            color = tint,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(6.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StupidEnglishBottomBarPreview() {
    StupidEnglishTheme {
        StupidEnglishBottomBar(
            tabs = listOf(
                NavigationKeys.BottomNavigationScreen.SE_WORDS,
                NavigationKeys.BottomNavigationScreen.SE_SETS,
            ),
            selectedTab = NavigationKeys.BottomNavigationScreen.SE_WORDS,
            effectFlow = null,
            onEventSent = {},
            onNavigationRequested = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StupidEnglishBottomNavigationItemPreview() {
    StupidEnglishTheme {
        StupidEnglishBottomNavigationItem(
            item = NavigationKeys.BottomNavigationScreen.SE_WORDS,
            selected = true,
            onSelected = {},
        )
    }
}