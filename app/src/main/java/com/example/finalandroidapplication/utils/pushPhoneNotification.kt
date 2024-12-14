package com.example.finalandroidapplication.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


fun pushPhoneNotification(context: Context, notifyId: String, title: String, description: String) {
    // Check for notification permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        // Do not send notification if permission is not granted
        return
    }

    val channelId = "notification_channel"

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            this.description = "Channel for app notifications"
        }

        if (notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(channel)
        }
    }

    val notificationDescription = description

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(notificationDescription)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    val uniqueId = notifyId.hashCode()
    notificationManager.notify(uniqueId, notification)
}
