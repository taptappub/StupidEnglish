package io.taptap.stupidenglish.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

fun <E> List<E>.getRandom(count: Int): List<E> {
    return asSequence().shuffled().take(count).toList()
}

/* val json = Uri.encode(Gson().toJson(navigationEffect.sentenceNavigation)) */

/* Gson().fromJson(value, SentenceNavigation::class.java) */
