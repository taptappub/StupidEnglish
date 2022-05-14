package io.taptap.stupidenglish.features.importwordstutorial.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ImportWordsTutorialViewModel @Inject constructor() : BaseViewModel<ImportWordsTutorialContract.Event, ImportWordsTutorialContract.State, ImportWordsTutorialContract.Effect>() {

    override fun setInitialState() = ImportWordsTutorialContract.State(
        pages = listOf(
            ImportWordsTutorialContract.Page.GOOGLE_SHEET,
            ImportWordsTutorialContract.Page.GOOGLE_TRANSLATER,
//            ImportWordsTutorialContract.Page.QUIZLET
        )
    )

    override suspend fun handleEvents(event: ImportWordsTutorialContract.Event) {
        when (event) {
            is ImportWordsTutorialContract.Event.OnPageChosen -> {
                val page = ImportWordsTutorialContract.Page.getByIndex(event.index)
                if (page.isSelected) return

                val pages = ArrayList(viewState.value.pages)
                pages
                    .clearSelectedPages()
                    .select(page)
                setState { copy(pages = pages) }
            }
            is ImportWordsTutorialContract.Event.ScrollToPage ->
                setEffect { ImportWordsTutorialContract.Effect.ScrollToPage(event.index) }
            is ImportWordsTutorialContract.Event.OnBackClick ->
                setEffect { ImportWordsTutorialContract.Effect.Navigation.BackToImportWords }
        }
    }
}

private fun List<ImportWordsTutorialContract.Page>.select(page: ImportWordsTutorialContract.Page) {
    this.find { it == page }?.isSelected = true
}

private fun List<ImportWordsTutorialContract.Page>.clearSelectedPages(): List<ImportWordsTutorialContract.Page> {
    this.forEach { it.isSelected = false }
    return this
}
