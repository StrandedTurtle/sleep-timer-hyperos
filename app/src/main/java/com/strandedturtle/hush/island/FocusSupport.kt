package com.strandedturtle.hush.island

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings

/** Probes for Xiaomi HyperOS focus notification / Super Island support. */
object FocusSupport {

    /** 0 = unsupported, 1 = HyperOS 1, 2 = HyperOS 2, 3 = HyperOS 3+ (Super Island). */
    fun protocolVersion(context: Context): Int =
        runCatching { Settings.System.getInt(context.contentResolver, "notification_focus_protocol", 0) }
            .getOrDefault(0)

    fun isXiaomi(): Boolean =
        Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true) ||
            Build.BRAND.equals("Redmi", ignoreCase = true) ||
            Build.BRAND.equals("POCO", ignoreCase = true)

    @SuppressLint("PrivateApi")
    fun hasIslandFeature(): Boolean = runCatching {
        val sp = Class.forName("android.os.SystemProperties")
        val get = sp.getDeclaredMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
        get.invoke(null, "persist.sys.feature.island", false) as Boolean
    }.getOrDefault(false)

    /** Whether HyperOS will render this app's focus notifications (per-app permission / whitelist). */
    fun canShowFocus(context: Context): Boolean = runCatching {
        val extras = Bundle().apply { putString("package", context.packageName) }
        context.contentResolver
            .call(Uri.parse("content://miui.statusbar.notification.public"), "canShowFocus", null, extras)
            ?.getBoolean("canShowFocus", false) ?: false
    }.getOrDefault(false)
}
