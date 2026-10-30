package com.strandedturtle.hush.timer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.strandedturtle.hush.media.MediaStopper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Handles the island / notification buttons and the expiry alarm. */
class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val step = TimerStore.settings(context).stepMinutes
        when (intent.action) {
            ACTION_PLUS -> SleepTimer.adjust(context, step)
            ACTION_MINUS -> SleepTimer.adjust(context, -step)
            ACTION_STOP -> SleepTimer.stop(context)
            ACTION_EXPIRE -> {
                // A late alarm for a timer that was extended: just re-arm it.
                val state = TimerStore.current(context)
                if (state.endAt - System.currentTimeMillis() > 2_000L) {
                    SleepTimer.restore(context)
                    return
                }
                SleepTimer.expire(context)
                val pending = goAsync()
                scope.launch {
                    try {
                        MediaStopper.stopAll(context.applicationContext, TimerStore.settings(context).fadeOut)
                    } finally {
                        pending.finish()
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_PLUS = "com.strandedturtle.hush.PLUS"
        const val ACTION_MINUS = "com.strandedturtle.hush.MINUS"
        const val ACTION_STOP = "com.strandedturtle.hush.STOP"
        const val ACTION_EXPIRE = "com.strandedturtle.hush.EXPIRE"

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        private fun requestCode(action: String) = when (action) {
            ACTION_PLUS -> 1
            ACTION_MINUS -> 2
            ACTION_STOP -> 3
            else -> 4
        }

        fun pendingIntent(context: Context, action: String): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode(action),
                SleepTimer.actionIntent(context, action),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
    }
}
