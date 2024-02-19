package com.fredy.mysavings.Data.Notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService:FirebaseMessagingService() {
    override fun onNewToken(token: String) { // for updating server
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) { // for responding to received message
        super.onMessageReceived(message)

    }
}