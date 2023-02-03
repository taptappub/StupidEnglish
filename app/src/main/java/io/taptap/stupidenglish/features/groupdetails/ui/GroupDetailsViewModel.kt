package io.taptap.stupidenglish.features.groupdetails.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.groupdetails.data.GroupDetailsRepository
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val repository: GroupDetailsRepository
) : BaseViewModel<GroupDetailsContract.Event, GroupDetailsContract.State, GroupDetailsContract.Effect>() {

    override fun setInitialState(): GroupDetailsContract.State {
        TODO("Not yet implemented")
    }

    override suspend fun handleEvents(event: GroupDetailsContract.Event) {
        when(event) {
            is GroupDetailsContract.Event.OnApplyDismiss -> TODO()
            is GroupDetailsContract.Event.OnBackClick -> TODO()
            is GroupDetailsContract.Event.OnRecover -> TODO()
            is GroupDetailsContract.Event.OnRecovered -> TODO()
            is GroupDetailsContract.Event.OnShareClick -> TODO()
            is GroupDetailsContract.Event.OnWordDismiss -> TODO()
        }
    }

}