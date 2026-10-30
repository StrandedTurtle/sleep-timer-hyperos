package com.strandedturtle.hush

import android.app.Application
import com.strandedturtle.hush.island.TimerNotification

class HushApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TimerNotification.createChannel(this)
    }
}
