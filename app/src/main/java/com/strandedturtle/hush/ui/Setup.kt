package com.strandedturtle.hush.ui

import android.app.AlarmManager
import android.app.StatusBarManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.strandedturtle.hush.R
import com.strandedturtle.hush.island.FocusSupport
import com.strandedturtle.hush.media.MediaListenerService
import com.strandedturtle.hush.media.MediaStopper
import com.strandedturtle.hush.tile.SleepTileService

enum class IslandStatus { READY, NEEDS_PERMISSION, NOT_HYPEROS }

data class SetupStatus(
    val notifications: Boolean,
    val island: IslandStatus,
    val protocol: Int,
    val mediaAccess: Boolean,
    val exactAlarms: Boolean,
    val isXiaomi: Boolean,
) {
    companion object {
        fun read(context: Context): SetupStatus {
            val protocol = FocusSupport.protocolVersion(context)
            val island = when {
                protocol <= 0 && !FocusSupport.hasIslandFeature() -> IslandStatus.NOT_HYPEROS
                FocusSupport.canShowFocus(context) -> IslandStatus.READY
                else -> IslandStatus.NEEDS_PERMISSION
            }
            return SetupStatus(
                notifications = NotificationManagerCompat.from(context).areNotificationsEnabled(),
                island = island,
                protocol = protocol,
                mediaAccess = MediaStopper.hasSessionAccess(context),
                exactAlarms = context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms(),
                isXiaomi = FocusSupport.isXiaomi(),
            )
        }
    }
}

object SetupActions {
    private fun Context.launch(vararg intents: Intent) {
        for (intent in intents) {
            try {
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return
            } catch (_: ActivityNotFoundException) {
            } catch (_: SecurityException) {
            }
        }
    }

    private fun appDetails(context: Context) =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))

    fun openNotificationSettings(context: Context) = context.launch(
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
        appDetails(context),
    )

    fun openMediaAccess(context: Context) {
        val component = ComponentName(context, MediaListenerService::class.java).flattenToString()
        context.launch(
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
                .putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME, component),
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
        )
    }

    /** MIUI / HyperOS kills alarms of apps without Autostart, so point people at it. */
    fun openAutostart(context: Context) = context.launch(
        Intent().setComponent(
            ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"),
        ),
        Intent("miui.intent.action.APP_PERM_EDITOR")
            .setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            .putExtra("extra_pkgname", context.packageName),
        appDetails(context),
    )

    fun openExactAlarms(context: Context) = context.launch(
        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}")),
        appDetails(context),
    )

    fun canRequestTile(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun requestTile(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        runCatching {
            context.getSystemService(StatusBarManager::class.java).requestAddTileService(
                ComponentName(context, SleepTileService::class.java),
                context.getString(R.string.tile_label),
                Icon.createWithResource(context, R.drawable.ic_tile_moon),
                context.mainExecutor,
            ) { }
        }
    }
}
