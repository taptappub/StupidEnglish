package io.taptap.stupidenglish.base.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
fun ModalBottomSheetState.hideSheet(scope: CoroutineScope) {
    scope.launch {
//        if (!isAnimationRunning) {
            hide()
//        }
    }
}

@ExperimentalMaterialApi
fun ModalBottomSheetState.showSheet(scope: CoroutineScope) {
    scope.launch {
//        if (!isAnimationRunning) {
        show()
//            animateTo(ModalBottomSheetValue.Expanded)
//        }
    }
}
