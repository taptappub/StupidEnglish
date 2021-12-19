package io.taptap.stupidenglish.base.logic.share

import android.content.Context
import android.content.Intent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareUtil @Inject constructor(
    private val context: Context
) {

    fun share(text: String) {
        val newText = "$text\n\nThis sentence was created by StupidEnglish app! #Education #StupidEnglish"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, newText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(shareIntent)
    }
}
