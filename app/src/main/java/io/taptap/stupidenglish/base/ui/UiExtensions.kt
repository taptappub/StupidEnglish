package io.taptap.stupidenglish.base.ui

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import io.taptap.stupidenglish.R

fun flipCard(
    context: Context,
    visibleView: View,
    inVisibleView: View,
    doOnStart: () -> Unit,
    doOnEnd: () -> Unit
) {
    try {
        visibleView.isVisible = true
        val scale = context.resources.displayMetrics.density
        val cameraDist = 8000 * scale
        visibleView.cameraDistance = cameraDist
        inVisibleView.cameraDistance = cameraDist
        val flipOutAnimatorSet =
            AnimatorInflater.loadAnimator(
                context,
                R.animator.flip_out
            ) as AnimatorSet
        flipOutAnimatorSet.setTarget(inVisibleView)
        val flipInAnimatorSet =
            AnimatorInflater.loadAnimator(
                context,
                R.animator.flip_in
            ) as AnimatorSet
        flipInAnimatorSet.setTarget(visibleView)
        flipOutAnimatorSet.doOnStart {
            doOnStart()
        }
        flipOutAnimatorSet.start()
        flipInAnimatorSet.start()
        flipInAnimatorSet.doOnEnd {
            doOnEnd()
            inVisibleView.isVisible = false
        }
    } catch (e: Exception) {
        Log.d("StupidEnglish", "flipCard + ${e.printStackTrace()}")
    }
}