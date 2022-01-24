package io.taptap.stupidenglish.features.alarm.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import java.util.*
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    private val alarmManager: AlarmManager,
    private val context: Context
) {

    companion object {
        const val ALARM_START = "io.taptap.stupidenglish.AlarmReceiver.start"
        const val ALARM_STOP = "io.taptap.stupidenglish.AlarmReceiver.stop"
    }

    private val alarmPendingIntent by lazy {
        val intent = Intent(context, AlarmReceiver::class.java).setAction(ALARM_START)
        PendingIntent.getBroadcast(context, 1000, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun enableNotifications() {
        Log.d("StupidEnglishState", "AlarmScheduler enableNotifications")
        context.enableReceiver<BootReceiver>()
        context.enableReceiver<AlarmReceiver>()
    }

    fun disableNotifications() {
        context.disableReceiver<BootReceiver>()
        context.disableReceiver<AlarmReceiver>()
    }

    fun schedulePushNotifications() {
        Log.d("StupidEnglishState", "AlarmScheduler schedulePushNotifications")
        enableNotifications()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmPendingIntent
        )
    }
}

inline fun <reified T : BroadcastReceiver> Context.enableReceiver() {
    val alarmReceiver = ComponentName(this, T::class.java)

    packageManager.setComponentEnabledSetting(
        alarmReceiver,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )
}

inline fun <reified T : BroadcastReceiver> Context.disableReceiver() {
    val alarmReceiver = ComponentName(this, T::class.java)

    packageManager.setComponentEnabledSetting(
        alarmReceiver,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}
