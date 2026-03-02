package com.example.blinkituserclone.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.appcompat.widget.DialogTitle
import androidx.core.app.NotificationCompat
import com.example.blinkituserclone.R
import com.example.blinkituserclone.activity.UsersMainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.remoteMessage

class FirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Notification Recieved")
        Log.d(TAG, "From: ${message.from}")
        Log.d(TAG, "Title: ${message.notification?.title}")
        Log.d(TAG, "Body: ${message.notification?.body}")

        message.notification?.let {
            showNotification(it.title ?: "New Order", it.body ?: "Order received!")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token: $token")
    }

    private fun showNotification(title: String, message: String) {
        val channerId = "order_updates"

        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, UsersMainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT)


        val notificationBuilder = NotificationCompat.Builder(this, channerId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(1000, 1000))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channerId,
                "order_updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Order Status Changed"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }


    companion object {
        private const val TAG = "UserFCMService"
    }
}