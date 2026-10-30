package com.strandedturtle.hush.tile

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.strandedturtle.hush.R
import com.strandedturtle.hush.timer.SleepTimer
import com.strandedturtle.hush.timer.TimerStore
import java.text.DateFormat
import java.util.Date

/** Tap: start the default timer (30 min) or cancel the running one. Long-press opens the app. */
class SleepTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        render()
    }

    override fun onClick() {
        super.onClick()
        if (TimerStore.current(this).isRunning) SleepTimer.stop(this) else SleepTimer.start(this)
        render()
    }

    private fun render() {
        val tile = qsTile ?: return
        val state = TimerStore.current(this)
        tile.icon = Icon.createWithResource(this, R.drawable.ic_tile_moon)
        tile.label = getString(R.string.tile_label)
        if (state.isRunning) {
            tile.state = Tile.STATE_ACTIVE
            tile.subtitle = "Ends " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(state.endAt))
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.subtitle = "${TimerStore.settings(this).defaultMinutes} min"
        }
        tile.updateTile()
    }

    companion object {
        fun refresh(context: Context) {
            runCatching {
                requestListeningState(context, ComponentName(context, SleepTileService::class.java))
            }
        }
    }
}
