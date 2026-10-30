package com.strandedturtle.hush.timer

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Snapshot of the timer. [endAt] and [totalMs] are 0 when no timer is running. */
data class TimerState(val endAt: Long = 0L, val totalMs: Long = 0L) {
    val isRunning: Boolean get() = endAt > System.currentTimeMillis()
    fun remainingMs(now: Long = System.currentTimeMillis()): Long = (endAt - now).coerceAtLeast(0L)
}

enum class IslandMode { AUTO, XIAOMI_FOCUS, LIVE_UPDATE }

data class Settings(
    val defaultMinutes: Int = 30,
    val stepMinutes: Int = 5,
    val fadeOut: Boolean = true,
    val islandMode: IslandMode = IslandMode.AUTO,
)

/** SharedPreferences-backed state shared by the tile, receivers, notification and UI. */
object TimerStore {
    private const val PREFS = "hush"
    private const val KEY_END = "end_at"
    private const val KEY_TOTAL = "total_ms"
    private const val KEY_DEFAULT = "default_minutes"
    private const val KEY_STEP = "step_minutes"
    private const val KEY_FADE = "fade_out"
    private const val KEY_ISLAND = "island_mode"

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    @Volatile private var prefs: SharedPreferences? = null

    // Keep a strong reference: SharedPreferences only holds listeners weakly.
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { p, _ -> publish(p) }

    private fun prefs(context: Context): SharedPreferences =
        prefs ?: synchronized(this) {
            prefs ?: context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).also {
                it.registerOnSharedPreferenceChangeListener(listener)
                publish(it)
                prefs = it
            }
        }

    private fun publish(p: SharedPreferences) {
        _state.value = TimerState(p.getLong(KEY_END, 0L), p.getLong(KEY_TOTAL, 0L))
        _settings.value = Settings(
            defaultMinutes = p.getInt(KEY_DEFAULT, 30),
            stepMinutes = p.getInt(KEY_STEP, 5),
            fadeOut = p.getBoolean(KEY_FADE, true),
            islandMode = runCatching { IslandMode.valueOf(p.getString(KEY_ISLAND, null)!!) }
                .getOrDefault(IslandMode.AUTO),
        )
    }

    fun init(context: Context) {
        prefs(context)
    }

    fun current(context: Context): TimerState {
        prefs(context)
        return _state.value
    }

    fun settings(context: Context): Settings {
        prefs(context)
        return _settings.value
    }

    fun save(context: Context, state: TimerState) {
        prefs(context).edit().putLong(KEY_END, state.endAt).putLong(KEY_TOTAL, state.totalMs).commit()
        _state.value = state
    }

    fun clear(context: Context) = save(context, TimerState())

    fun setDefaultMinutes(context: Context, minutes: Int) =
        prefs(context).edit().putInt(KEY_DEFAULT, minutes.coerceIn(5, 600)).apply()

    fun setFadeOut(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_FADE, enabled).apply()

    fun setIslandMode(context: Context, mode: IslandMode) =
        prefs(context).edit().putString(KEY_ISLAND, mode.name).apply()
}
