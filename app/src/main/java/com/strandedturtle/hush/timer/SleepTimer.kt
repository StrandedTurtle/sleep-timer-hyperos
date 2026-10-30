package com.strandedturtle.hush.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.strandedturtle.hush.island.TimerNotification
import com.strandedturtle.hush.tile.SleepTileService

/** Every entry point (tile, island buttons, app) funnels through here. */
object SleepTimer {
    private const val MINUTE = 60_000L
    private const val MIN_REMAINING = MINUTE
    private const val MAX_TOTAL = 12 * 60 * MINUTE

    fun start(context: Context, minutes: Int = TimerStore.settings(context).defaultMinutes) {
        val total = (minutes * MINUTE).coerceIn(MIN_REMAINING, MAX_TOTAL)
        apply(context, TimerState(System.currentTimeMillis() + total, total), firstPost = true)
    }

    /** Adds (or with a negative value removes) minutes. Never drops below one minute left. */
    fun adjust(context: Context, deltaMinutes: Int) {
        val state = TimerStore.current(context)
        if (!state.isRunning) {
            if (deltaMinutes > 0) start(context, deltaMinutes)
            return
        }
        val now = System.currentTimeMillis()
        val remaining = (state.remainingMs(now) + deltaMinutes * MINUTE).coerceIn(MIN_REMAINING, MAX_TOTAL)
        val elapsed = state.totalMs - state.remainingMs(now)
        val total = (elapsed + remaining).coerceAtLeast(remaining)
        apply(context, TimerState(now + remaining, total), firstPost = false)
    }

    fun stop(context: Context) {
        alarmManager(context).cancel(expirePendingIntent(context))
        TimerStore.clear(context)
        TimerNotification.cancel(context)
        SleepTileService.refresh(context)
    }

    /** Called by the alarm: clear state; the receiver then stops playback. */
    fun expire(context: Context) {
        TimerStore.clear(context)
        TimerNotification.cancel(context)
        SleepTileService.refresh(context)
    }

    /** Re-arm after reboot / app update, or tidy up if the deadline already passed. */
    fun restore(context: Context) {
        val state = TimerStore.current(context)
        if (state.isRunning) apply(context, state, firstPost = false) else if (state.endAt != 0L) stop(context)
    }

    private fun apply(context: Context, state: TimerState, firstPost: Boolean) {
        TimerStore.save(context, state)
        schedule(context, state.endAt)
        TimerNotification.post(context, state, firstPost)
        SleepTileService.refresh(context)
    }

    private fun schedule(context: Context, at: Long) {
        val am = alarmManager(context)
        val pi = expirePendingIntent(context)
        if (am.canScheduleExactAlarms()) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        }
    }

    private fun alarmManager(context: Context) = context.getSystemService(AlarmManager::class.java)

    private fun expirePendingIntent(context: Context): PendingIntent =
        TimerReceiver.pendingIntent(context, TimerReceiver.ACTION_EXPIRE)

    fun actionIntent(context: Context, action: String): Intent =
        Intent(context, TimerReceiver::class.java).setAction(action)
}
