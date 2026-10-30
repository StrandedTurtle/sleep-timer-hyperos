package com.strandedturtle.hush.media

import android.service.notification.NotificationListenerService

/**
 * Intentionally empty. Being an enabled notification listener is what lets
 * [MediaStopper] see and pause every app's media session (video included).
 */
class MediaListenerService : NotificationListenerService()
