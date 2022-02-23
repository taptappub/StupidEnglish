package io.taptap.stupidenglish.features.stack.ui.adapter

import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import kotlin.random.Random

fun SwipeAnimationSetting.Builder.randomDirection(): SwipeAnimationSetting.Builder {
    val direction = when (Random.nextInt(0, 4)) {
        0 -> Direction.Left
        1 -> Direction.Top
        2 -> Direction.Right
        3 -> Direction.Bottom
        else -> Direction.Right
    }
    return setDirection(direction)
}