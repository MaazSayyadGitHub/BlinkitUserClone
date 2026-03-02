package com.example.blinkituserclone.Notification.api.models

data class Message(
    val token: String,
    val notification: NotificationBody
)

// this is JSON Request we will send to FCM Server
//{
//    "message": {
//    "token": "fcm_token_here",
//    "notification": {
//        "title": "Order",
//        "body": "New order!"
//    }
//}
//}