package io.taptap.stupidenglish.features.termsandconditions.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class TermsViewModel @Inject constructor() :
    BaseViewModel<TermsContract.Event, TermsContract.State, TermsContract.Effect>() {

    override fun setInitialState() = TermsContract.State(
        termsUrl = "https://sites.google.com/view/stupidenglishapp-privacy"
    )

    override suspend fun handleEvents(event: TermsContract.Event) {
        when (event) {
            TermsContract.Event.OnBackClick ->
                setEffect { TermsContract.Effect.Navigation.BackToProfile }
        }
    }
}
