package io.taptap.stupidenglish.features.groupdetails.ui

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun GroupDetailsScreen(
    context: Context,
    state: GroupDetailsContract.State,
    effectFlow: Flow<GroupDetailsContract.Effect>?,
    onEventSent: (event: GroupDetailsContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: GroupDetailsContract.Effect.Navigation) -> Unit
) {

}