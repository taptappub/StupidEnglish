package io.taptap.stupidenglish.base.logic.share

import android.content.Context
import android.content.Intent
import io.taptap.stupidenglish.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareUtil @Inject constructor(
    private val context: Context
) {

    fun share(text: String) {
        val newText = context.getString(R.string.adds_share_text, text)
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
