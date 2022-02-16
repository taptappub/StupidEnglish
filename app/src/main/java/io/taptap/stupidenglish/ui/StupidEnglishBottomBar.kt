package io.taptap.stupidenglish.ui

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.features.main.ui.MainContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun StupidEnglishBottomBar(
    currentRoute: String,
    state: MainContract.State,
    effectFlow: Flow<MainContract.Effect>?,
    onEventSent: (event: MainContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit
) {
    val tabs = state.bottomBarTabs
    Log.d("TAPTAPTAP", "StupidEnglishBottomBar tabs = $tabs, currentRoute = $currentRoute")
    val currentSection = tabs.first { it.route == currentRoute }

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

    NavigationBar {
        tabs.forEach { item ->
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                label = { Text(text = stringResource(id = item.title)) },
                selected = item == currentSection,
                onClick = {
                    onEventSent(MainContract.Event.OnTabSelected(item))
                }
            )
        }
    }
}
