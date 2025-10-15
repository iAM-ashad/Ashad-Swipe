package com.iamashad.ashad_swipe.util

import android.content.Context
import android.os.Build
import androidx.core.content.edit

/**
 * Simple SharedPreferences wrapper to track whether the user
 * has been asked or declined notification permission.
 *
 * Used to avoid re-prompting unnecessarily on Android 13+.
 */
object NotificationPrefs {
    private const val FILE = "notif_prefs"
    private const val KEY_ASKED = "asked_once"
    private const val KEY_DECLINED = "declined"

    /** Returns this app’s private SharedPreferences file. */
    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    /**
     * Whether to show a custom rationale dialog before requesting permission.
     *
     * Shown only once, and only on Android 13 (Tiramisu) or later.
     */
    fun shouldShowRationale(ctx: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        val s = prefs(ctx)
        val asked = s.getBoolean(KEY_ASKED, false)
        val declined = s.getBoolean(KEY_DECLINED, false)
        return !asked && !declined
    }

    /** Marks that the user was prompted for permission at least once. */
    fun markAsked(ctx: Context) {
        prefs(ctx).edit { putBoolean(KEY_ASKED, true) }
    }

    /** Marks that the user explicitly declined notifications. */
    fun markDeclined(ctx: Context) {
        prefs(ctx).edit { putBoolean(KEY_DECLINED, true) }
    }
}
