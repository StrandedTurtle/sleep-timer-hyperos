package com.strandedturtle.hush.island

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.strandedturtle.hush.R
import com.strandedturtle.hush.timer.TimerReceiver
import com.strandedturtle.hush.timer.TimerState
import com.strandedturtle.hush.timer.TimerStore
import com.strandedturtle.hush.ui.MainActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.util.Date

/**
 * The running-timer notification. On HyperOS it carries `miui.focus.param` so the system draws it
 * as a Super Island: moon + live countdown in the pill, and −/+/Stop buttons when expanded.
 * Elsewhere (or if Xiaomi denies focus permission) it is an Android 16 Live Update with the same actions.
 */
object TimerNotification {
    private const val CHANNEL_ID = "sleep_timer"
    private const val NOTIFICATION_ID = 30

    private const val PIC_MOON = "miui.focus.pic_moon"
    private const val ACT_STOP = "miui.focus.action_stop"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.channel_desc)
            setSound(null, null)
            enableVibration(false)
            setShowBadge(false)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun cancel(context: Context) {
        context.getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    }

    fun post(context: Context, state: TimerState, firstPost: Boolean) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        val settings = TimerStore.settings(context)
        val step = settings.stepMinutes
        val useFocus = FocusSupport.protocolVersion(context) > 0 && FocusSupport.canShowFocus(context)

        val endText = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(state.endAt))
        val minus = TimerReceiver.pendingIntent(context, TimerReceiver.ACTION_MINUS)
        val plus = TimerReceiver.pendingIntent(context, TimerReceiver.ACTION_PLUS)
        val stop = TimerReceiver.pendingIntent(context, TimerReceiver.ACTION_STOP)
        val open = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tile_moon)
            .setContentTitle("Sleep timer")
            .setContentText("Media stops at $endText")
            .setSubText("Hush")
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .setOngoing(true)
            .setOnlyAlertOnce(!firstPost)
            .setSilent(true)
            .setWhen(state.endAt)
            .setShowWhen(true)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setContentIntent(open)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_minus, "−$step min", minus)
            .addAction(R.drawable.ic_plus, "+$step min", plus)
            .addAction(R.drawable.ic_stop, "Stop", stop)

        if (useFocus) {
            builder.addExtras(focusResources(context, stop))
        } else {
            // Android 16 Live Update: shows as a status bar chip / island on supporting skins.
            builder.setRequestPromotedOngoing(true)
        }

        val notification = builder.build()
        if (useFocus) {
            notification.extras.putString(
                "miui.focus.param",
                focusParam(state, step, endText, firstPost, FocusSupport.protocolVersion(context)),
            )
        }
        context.getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
    }

    /** `miui.focus.pics` and `miui.focus.actions` bundles referenced by key from the JSON. */
    private fun focusResources(
        context: Context,
        stop: PendingIntent,
    ): Bundle {
        val pics = Bundle().apply {
            putParcelable(PIC_MOON, Icon.createWithResource(context, R.drawable.ic_island_moon))
        }
        fun action(icon: Int, title: String, pi: PendingIntent) =
            Notification.Action.Builder(Icon.createWithResource(context, icon), title, pi).build()
        val actions = Bundle().apply {
            putParcelable(ACT_STOP, action(R.drawable.ic_stop, "Stop", stop))
        }
        return Bundle().apply {
            putBundle("miui.focus.pics", pics)
            putBundle("miui.focus.actions", actions)
        }
    }

    private fun timerInfo(state: TimerState, now: Long) = JSONObject()
        .put("timerType", -1) // count down
        .put("timerWhen", state.endAt)
        .put("timerTotal", state.totalMs)
        .put("timerSystemCurrent", now)

    private fun textAction(key: String, title: String, bg: String, titleColor: String) = JSONObject()
        .put("type", 2)
        .put("action", key)
        .put("actionTitle", title)
        .put("actionIntentType", 2) // broadcast
        .put("actionBgColor", bg)
        .put("actionBgColorDark", bg)
        .put("actionTitleColor", titleColor)
        .put("actionTitleColorDark", titleColor)

    internal fun focusParam(
        state: TimerState,
        step: Int,
        endText: String,
        firstPost: Boolean,
        protocol: Int,
    ): String {
        val now = System.currentTimeMillis()
        val moon = JSONObject().put("type", 1).put("pic", PIC_MOON)

        val island = JSONObject()
            .put("islandProperty", 1)
            .put("islandPriority", 2)
            .put("dismissIsland", false)
            .put("needCloseAnimation", true)
            .put("highlightColor", "#8FA8FF")
            .put(
                "bigIslandArea",
                JSONObject()
                    .put("imageTextInfoLeft", JSONObject().put("type", 1).put("picInfo", moon))
                    .put(
                        "sameWidthDigitInfo",
                        JSONObject().put("timerInfo", timerInfo(state, now)).put("showHighlightColor", true),
                    ),
            )
            .put("smallIslandArea", JSONObject().put("picInfo", moon))

        val chat = JSONObject()
            .put("type", 1)
            .put("title", "Sleep timer · ends $endText")
            .put("picProfile", PIC_MOON)
            .put("timerInfo", timerInfo(state, now))

        // The expanded island renders a single button.
        val actions = JSONArray().put(textAction(ACT_STOP, "Stop", "#FF5B6CFF", "#FFFFFFFF"))

        val v2 = JSONObject()
            .put("protocol", if (protocol > 0) protocol else 3)
            .put("business", "sleep_timer")
            .put("scene", "timer")
            .put("updatable", true)
            .put("ticker", "Sleep · $endText")
            .put("tickerPic", PIC_MOON)
            .put("enableFloat", firstPost)
            .put("islandFirstFloat", firstPost)
            .put("isShowNotification", true)
            .put("aodTitle", "Sleep · $endText")
            .put("aodPic", PIC_MOON)
            .put("chatInfo", chat)
            .put("actions", actions)
            .put("param_island", island)

        return JSONObject().put("param_v2", v2).toString()
    }
}
