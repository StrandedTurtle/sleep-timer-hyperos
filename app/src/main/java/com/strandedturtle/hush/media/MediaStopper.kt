package com.strandedturtle.hush.media

import android.content.ComponentName
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.SystemClock
import android.view.KeyEvent
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.delay

/** Stops whatever is playing: media sessions, then audio focus, then a pause key as a last resort. */
object MediaStopper {
    private const val FADE_MS = 6_000L
    private const val SETTLE_MS = 1_200L

    fun hasSessionAccess(context: Context): Boolean =
        NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)

    suspend fun stopAll(context: Context, fade: Boolean) {
        val audio = context.getSystemService(AudioManager::class.java)
        val stream = AudioManager.STREAM_MUSIC
        val original = audio.getStreamVolume(stream)
        val faded = fade && original > 0 && audio.isMusicActive

        if (faded) {
            val stepDelay = (FADE_MS / original).coerceAtLeast(40L)
            for (v in original - 1 downTo 0) {
                setVolume(audio, stream, v)
                delay(stepDelay)
            }
        }

        val pausedSessions = pauseSessions(context)
        val focus = grabFocus(audio)
        if (pausedSessions == 0) sendPauseKey(audio)

        delay(SETTLE_MS)
        focus?.let { audio.abandonAudioFocusRequest(it) }

        // If something ignored every request, leave it muted rather than blasting back at full volume.
        if (faded && !audio.isMusicActive) setVolume(audio, stream, original)
    }

    private fun pauseSessions(context: Context): Int {
        if (!hasSessionAccess(context)) return 0
        val msm = context.getSystemService(MediaSessionManager::class.java) ?: return 0
        val controllers = try {
            msm.getActiveSessions(ComponentName(context, MediaListenerService::class.java))
        } catch (_: SecurityException) {
            return 0
        }
        var count = 0
        for (controller in controllers) {
            val playing = when (controller.playbackState?.state) {
                PlaybackState.STATE_PLAYING, PlaybackState.STATE_BUFFERING,
                PlaybackState.STATE_FAST_FORWARDING, PlaybackState.STATE_REWINDING -> true
                else -> false
            }
            if (!playing) continue
            runCatching { controller.transportControls.pause() }
            count++
        }
        return count
    }

    private fun grabFocus(audio: AudioManager): AudioFocusRequest? = runCatching {
        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            .setOnAudioFocusChangeListener { }
            .build()
        audio.requestAudioFocus(request)
        request
    }.getOrNull()

    private fun sendPauseKey(audio: AudioManager) {
        val now = SystemClock.uptimeMillis()
        audio.dispatchMediaKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0))
        audio.dispatchMediaKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE, 0))
    }

    private fun setVolume(audio: AudioManager, stream: Int, value: Int) {
        runCatching { audio.setStreamVolume(stream, value, 0) }
    }
}
