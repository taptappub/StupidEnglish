package io.taptap.stupidenglish.features.importwordstutorial.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.sources.groups.read.GroupItemUI
import io.taptap.stupidenglish.base.logic.sources.groups.read.GroupListModels
import io.taptap.stupidenglish.base.logic.sources.groups.read.NoGroup
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.importwords.domain.ImportWordsInteractor
import io.taptap.stupidenglish.features.importwordstutorial.data.ImportWordsTutorialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import taptap.pub.doOnComplete
import taptap.pub.doOnError
import taptap.pub.doOnSuccess
import taptap.pub.fold
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class ImportWordsTutorialViewModel @Inject constructor(
    private val repository: ImportWordsTutorialRepository
) : BaseViewModel<ImportWordsTutorialContract.Event, ImportWordsTutorialContract.State, ImportWordsTutorialContract.Effect>() {

    override fun setInitialState() = ImportWordsTutorialContract.State(
        link = ""
    )

    override suspend fun handleEvents(event: ImportWordsTutorialContract.Event) {
//        when (this) {
//            это вроде можно вообще дулаить
//        }
    }
}
