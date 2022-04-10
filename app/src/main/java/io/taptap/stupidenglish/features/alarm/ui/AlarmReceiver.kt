package io.taptap.stupidenglish.features.alarm.ui

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.app.TaskStackBuilder
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import io.taptap.notify.Notify
import io.taptap.stupidenglish.MainActivity
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.URI
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.alarm.data.AlarmRepository
import kotlinx.coroutines.*
import taptap.pub.takeOrNull
import javax.inject.Inject

const val ALARM_NOTIFICATION_ID = 3957

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmRepository: AlarmRepository
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("StupidEnglishState", "AlarmReceiver")
        when (intent.action) {
            AlarmScheduler.ALARM_START -> alarmStart(context)
            AlarmScheduler.ALARM_STOP -> alarmStop()
        }
    }

    private fun alarmStop() {
        coroutineScope.cancel()
    }

    private fun alarmStart(context: Context) {
        coroutineScope.launch {
            val words = alarmRepository.getRandomWords(3).takeOrNull()
            if (words != null) {
                withContext(Dispatchers.Main) {
                    showPushNotification(context, words)
                }
            }
        }
    }

    private fun showPushNotification(context: Context, words: List<Word>) {
        val pendingIntent = createPendingIntent(context, words)
        val string = words.joinToString(",") { it.word }

        Notify
            .with(context)
            .meta {
                // Launch the MainActivity once the notification is clicked.
                clickIntent = pendingIntent
                // Start a service which clears the badge count once the notification is dismissed.
                /*clearIntent = PendingIntent.getService(context, todo это бы в аналитику
                    0,
                    Intent(context, MyNotificationService::class.java)
                        .putExtra("action", "clear_badges"),
                    0)*/
            }
            .header {
                icon = R.drawable.ic_notification
                color = Color.YELLOW
            }
            .content {
                title = context.getString(R.string.alrm_sentence_title)
                text = context.getString(R.string.alrm_sentence_message, string)
            }
            .show(ALARM_NOTIFICATION_ID)
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun createPendingIntent(context: Context, words: List<Word>): PendingIntent {
        val string = words.joinToString(",") { it.id.toString() }
        val taskDetailIntent = Intent(
            Intent.ACTION_VIEW,
            "$URI/${NavigationKeys.Arg.WORDS_ID}=$string".toUri(),
            context,
            MainActivity::class.java
        )

        val requestCode = 231

        val pending: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(taskDetailIntent)
            getPendingIntent(requestCode, PendingIntent.FLAG_IMMUTABLE)!!
        }
        return pending
    }
}
