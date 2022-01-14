package io.taptap.stupidenglish.features.alarm.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("StupidEnglishState", "BootReceiver")
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            alarmScheduler.schedulePushNotifications()
        }
    }
}