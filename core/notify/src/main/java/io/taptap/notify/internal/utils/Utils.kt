package io.taptap.notify.internal.utils

import android.text.Html
import java.util.Random

internal object Utils {
    fun getRandomInt(): Int {
        return Random(System.currentTimeMillis()).nextInt()
    }

    fun getAsSecondaryFormattedText(str: String?): CharSequence? {
        str ?: return null

        return Html.fromHtml("<font color='#3D3D3D'>$str</font>")
    }
}
