package com.strandedturtle.hush.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strandedturtle.hush.R
import com.strandedturtle.hush.timer.IslandMode
import com.strandedturtle.hush.timer.SleepTimer
import com.strandedturtle.hush.timer.TimerStore
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date

private val PRESETS = listOf(15, 30, 45, 60, 90)

@Composable
fun HushScreen() {
    val context = LocalContext.current
    val state by TimerStore.state.collectAsStateWithLifecycle()
    val settings by TimerStore.settings.collectAsStateWithLifecycle()

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(250)
        }
    }
    val running = state.endAt > now

    // Minutes picked while idle; starts out at the default the tile uses.
    var pending by rememberSaveable { mutableIntStateOf(settings.defaultMinutes) }

    var setup by remember { mutableStateOf(SetupStatus.read(context)) }
    LifecycleResumeEffect(Unit) {
        setup = SetupStatus.read(context)
        onPauseOrDispose { }
    }
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        setup = SetupStatus.read(context)
    }
    fun ensureNotifications() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    val step = settings.stepMinutes
    val remaining = if (running) state.remainingMs(now) else pending * 60_000L
    val fraction = if (running && state.totalMs > 0) remaining.toFloat() / state.totalMs else 1f
    val caption = if (running) {
        "Ends at " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(state.endAt))
    } else {
        "Ready when you are"
    }

    val dial: @Composable () -> Unit = {
        TimerControls(
            timeText = formatClock(remaining),
            caption = caption,
            fraction = fraction,
            running = running,
            step = step,
            selectedPreset = if (running) null else pending,
            onMinus = { if (running) SleepTimer.adjust(context, -step) else pending = (pending - step).coerceAtLeast(5) },
            onPlus = { if (running) SleepTimer.adjust(context, step) else pending = (pending + step).coerceAtMost(600) },
            onPrimary = {
                if (running) {
                    SleepTimer.stop(context)
                } else {
                    ensureNotifications()
                    SleepTimer.start(context, pending)
                }
            },
            onPreset = { minutes ->
                pending = minutes
                if (running) SleepTimer.start(context, minutes)
            },
        )
    }
    val panels: @Composable () -> Unit = {
        SetupPanel(setup, onNotifications = {
            ensureNotifications()
            SetupActions.openNotificationSettings(context)
        })
        Spacer(Modifier.height(16.dp))
        SettingsPanel(
            defaultMinutes = settings.defaultMinutes,
            fadeOut = settings.fadeOut,
            islandMode = settings.islandMode,
            onDefault = { TimerStore.setDefaultMinutes(context, it) },
            onFade = { TimerStore.setFadeOut(context, it) },
            onIslandMode = { TimerStore.setIslandMode(context, it) },
        )
    }

    AuroraBackground {
        BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            if (maxWidth >= 720.dp) {
                // Tablet / unfolded: dial on the left, settings on the right.
                Row(Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp)) {
                    Column(
                        Modifier.weight(1f).fillMaxSize().verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Header()
                        Spacer(Modifier.height(16.dp))
                        Box(Modifier.widthIn(max = 460.dp)) { dial() }
                    }
                    Spacer(Modifier.size(32.dp))
                    Column(
                        Modifier.weight(1f).fillMaxSize().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                    ) { panels() }
                }
            } else {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Header()
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.widthIn(max = 420.dp)) { dial() }
                    Spacer(Modifier.height(24.dp))
                    panels()
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun Header() {
    val glass = LocalGlass.current
    Column(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Hush", color = glass.text, fontSize = 34.sp, fontWeight = FontWeight.SemiBold)
        Text("Sleep timer for everything that plays", color = glass.textMuted, fontSize = 14.sp)
    }
}

@Composable
private fun TimerControls(
    timeText: String,
    caption: String,
    fraction: Float,
    running: Boolean,
    step: Int,
    selectedPreset: Int?,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onPrimary: () -> Unit,
    onPreset: (Int) -> Unit,
) {
    val glass = LocalGlass.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TimerDial(fraction = fraction, timeText = timeText, caption = caption)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top,
        ) {
            GlassIconButton(R.drawable.ic_minus, "−$step min", onMinus)
            GradientPill(
                text = if (running) "Stop" else "Start",
                colors = if (running) listOf(glass.danger, glass.dangerAlt) else listOf(glass.accent, glass.accentAlt),
                onClick = onPrimary,
            )
            GlassIconButton(R.drawable.ic_plus, "+$step min", onPlus)
        }
        Spacer(Modifier.height(20.dp))
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            ChipRow {
                PRESETS.forEach { m ->
                    GlassChip(if (m < 60) "$m min" else "${m / 60}h${if (m % 60 != 0) " ${m % 60}" else ""}", selectedPreset == m, { onPreset(m) })
                }
            }
        }
    }
}

@Composable
private fun SetupPanel(setup: SetupStatus, onNotifications: () -> Unit) {
    val context = LocalContext.current
    GlassPanel(Modifier.fillMaxWidth()) {
        SectionTitle("Setup")
        StatusRow(
            title = "Notifications",
            detail = if (setup.notifications) "Allowed" else "Needed for the island and its controls",
            ok = setup.notifications,
            action = if (setup.notifications) null else "Allow",
            onAction = onNotifications,
        )
        RowDivider()
        StatusRow(
            title = "Super Island",
            detail = when (setup.island) {
                IslandStatus.READY -> "HyperOS focus protocol v${setup.protocol} · ready"
                IslandStatus.NEEDS_PERMISSION -> "Turn on focus / island notifications for Hush"
                IslandStatus.NOT_HYPEROS -> "Not detected · using Android Live Updates"
            },
            ok = setup.island == IslandStatus.READY,
            warn = setup.island == IslandStatus.NOT_HYPEROS,
            action = if (setup.island == IslandStatus.NEEDS_PERMISSION) "Open" else null,
            onAction = { SetupActions.openNotificationSettings(context) },
        )
        RowDivider()
        StatusRow(
            title = "Pause any app",
            detail = if (setup.mediaAccess) "Can pause every audio & video session" else "Grant media control so video apps stop too",
            ok = setup.mediaAccess,
            action = if (setup.mediaAccess) null else "Grant",
            onAction = { SetupActions.openMediaAccess(context) },
        )
        RowDivider()
        StatusRow(
            title = "Exact alarms",
            detail = if (setup.exactAlarms) "Timer fires on the dot" else "Allow so the timer isn't delayed",
            ok = setup.exactAlarms,
            action = if (setup.exactAlarms) null else "Allow",
            onAction = { SetupActions.openExactAlarms(context) },
        )
        if (setup.isXiaomi) {
            RowDivider()
            StatusRow(
                title = "Autostart",
                detail = "Recommended so HyperOS doesn't block the alarm",
                ok = null,
                action = "Open",
                onAction = { SetupActions.openAutostart(context) },
            )
        }
        if (SetupActions.canRequestTile()) {
            RowDivider()
            StatusRow(
                title = "Quick tile",
                detail = "Tap starts ${TimerStore.settings(context).defaultMinutes} min · tap again to cancel",
                ok = null,
                action = "Add",
                onAction = { SetupActions.requestTile(context) },
            )
        }
    }
}

@Composable
private fun SettingsPanel(
    defaultMinutes: Int,
    fadeOut: Boolean,
    islandMode: IslandMode,
    onDefault: (Int) -> Unit,
    onFade: (Boolean) -> Unit,
    onIslandMode: (IslandMode) -> Unit,
) {
    val glass = LocalGlass.current
    GlassPanel(Modifier.fillMaxWidth()) {
        SectionTitle("Preferences")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Tile duration", color = glass.text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text("What one tap on the tile starts", color = glass.textMuted, fontSize = 13.sp)
            }
            GlassChip("−", false, { onDefault(defaultMinutes - 5) })
            Text(
                "$defaultMinutes min",
                color = glass.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            GlassChip("+", false, { onDefault(defaultMinutes + 5) })
        }
        RowDivider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Fade out", color = glass.text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text("Lower the volume gently before pausing", color = glass.textMuted, fontSize = 13.sp)
            }
            Switch(
                checked = fadeOut,
                onCheckedChange = onFade,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = glass.accent,
                    checkedThumbColor = Color.White,
                    uncheckedTrackColor = glass.fillTop,
                    uncheckedBorderColor = glass.rimTop,
                    uncheckedThumbColor = glass.textMuted,
                ),
            )
        }
        RowDivider()
        Text("Island style", color = glass.text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text("Auto uses Xiaomi Super Island when HyperOS allows it", color = glass.textMuted, fontSize = 13.sp)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            ChipRow {
                GlassChip("Auto", islandMode == IslandMode.AUTO, { onIslandMode(IslandMode.AUTO) })
                GlassChip("Super Island", islandMode == IslandMode.XIAOMI_FOCUS, { onIslandMode(IslandMode.XIAOMI_FOCUS) })
                GlassChip("Live Update", islandMode == IslandMode.LIVE_UPDATE, { onIslandMode(IslandMode.LIVE_UPDATE) })
            }
        }
    }
}

@Composable
private fun StatusRow(
    title: String,
    detail: String,
    ok: Boolean?,
    action: String?,
    onAction: () -> Unit,
    warn: Boolean = false,
) {
    val glass = LocalGlass.current
    val dot = when {
        ok == true -> glass.good
        warn || ok == null -> glass.warn
        else -> glass.danger
    }
    Row(
        Modifier.fillMaxWidth().clickable(enabled = action != null, onClick = onAction).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(10.dp).background(dot, CircleShape))
        Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
            Text(title, color = glass.text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(detail, color = glass.textMuted, fontSize = 13.sp)
        }
        if (action != null) GlassChip(action, true, onAction)
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        Modifier.padding(vertical = 12.dp),
        thickness = 1.dp,
        color = LocalGlass.current.text.copy(alpha = 0.08f),
    )
}

private fun formatClock(ms: Long): String {
    val totalSec = (ms + 999) / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}
