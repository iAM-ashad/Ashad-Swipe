package com.iamashad.ashad_swipe.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.iamashad.ashad_swipe.R

object NotificationHelper {

    private const val CHANNEL_ID = "product_updates"
    private const val CHANNEL_NAME = "Product updates"
    private const val CHANNEL_DESC = "Notifications when products are added or synced"
    private const val NOTIF_ID_PRODUCT_ADDED = 1001
    private const val NOTIF_ID_PENDING_SYNCED = 1002

    /** Create/update the channel. Call once (e.g., Application.onCreate). */
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            mgr.createNotificationChannel(channel)
        }
    }

    /** Runtime permission check for Android 13+. */
    private fun hasPostPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    /** Launches the app when the user taps the notification. */
    private fun defaultContentIntent(context: Context): PendingIntent? {
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
        return launch?.let {
            PendingIntent.getActivity(
                context,
                0,
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    /** Base builder with common styling. */
    private fun baseBuilder(context: Context, text: String): NotificationCompat.Builder {
        // Try to tint with brand color (safe on all APIs)
        val accent = ContextCompat.getColor(context, R.color.purple_500)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setColor(accent)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(defaultContentIntent(context))
    }

    /** “Product added” toast-in-a-tray. */
    @SuppressLint("MissingPermission")
    fun notifyProductAdded(context: Context, productName: String) {
        if (!hasPostPermission(context)) return
        val nm = NotificationManagerCompat.from(context)
        if (!nm.areNotificationsEnabled()) return

        ensureChannel(context)
        val text = "“$productName” was added successfully."
        val notif = baseBuilder(context, text).build()

        try {
            nm.notify(NOTIF_ID_PRODUCT_ADDED, notif)
        } catch (_: SecurityException) {
            // Permission revoked between check and post — ignore safely.
        }
    }

    /** “Pending uploads synced” summary. */
    @SuppressLint("MissingPermission")
    fun notifyPendingSynced(context: Context, count: Int) {
        if (!hasPostPermission(context)) return
        val nm = NotificationManagerCompat.from(context)
        if (!nm.areNotificationsEnabled()) return

        ensureChannel(context)
        val text = "Uploaded $count pending item(s)."
        val notif = baseBuilder(context, text).build()

        try {
            nm.notify(NOTIF_ID_PENDING_SYNCED, notif)
        } catch (_: SecurityException) {
            // Permission revoked between check and post — ignore safely.
        }
    }
}
