package io.taptap.stupidenglish.features.importwordstutorial.ui

import androidx.annotation.StringRes
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class ImportWordsTutorialContract {
    sealed class Event : ViewEvent {
        data class OnPageChosen(val index: Int) : Event()
        data class ScrollToPage(val index: Int) : Event()
    }

    data class State(
        val pages: List<Page>,
    ) : ViewState

    enum class Page(
        val index: Int,
        @StringRes val titleRes: Int,
        var isSelected: Boolean
    ) {
        GOOGLE_SHEET(0, R.string.imwt_google_sheet_title, false),
        GOOGLE_TRANSLATER(1, R.string.imwt_google_translater_title, false);
        //QUIZLET(2, R.string.imwt_quizlet_title, false);

        companion object {
            fun getByIndex(index: Int): Page = values().find { it.index == index }
                ?: error("Page with index $index not found")
        }
    }

    sealed class Effect : ViewSideEffect {
        data class ScrollToPage(val index: Int) : Effect()

        sealed class Navigation : Effect() {
            object BackToImportWords : Navigation()
        }
    }
}