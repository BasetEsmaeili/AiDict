package com.baset.anki.pro.generator.presentation.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.baset.anki.pro.generator.R
import com.baset.anki.pro.generator.presentation.ui.main.MainActivity

object NotificationsHelper {

    private const val NOTIFICATION_CHANNEL_ID = "ai_dict_notification"

    @JvmStatic
    fun createNotificationChannel(context: Context) {
        val notificationManager = ContextCompat.getSystemService(
            context.applicationContext,
            NotificationManager::class.java
        )
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager?.createNotificationChannel(channel)
    }

    @JvmStatic
    fun buildWindowServiceNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.description_window_service_notification))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(Intent(context, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            })
            .build()
    }
}