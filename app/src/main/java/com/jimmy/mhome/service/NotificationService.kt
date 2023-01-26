package com.jimmy.mhome.service

import androidx.annotation.WorkerThread
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jimmy.util.Logger
import timber.log.Timber

class NotificationService: FirebaseMessagingService() {
    private val TAG = "NotificationService"

    @WorkerThread
    override fun onNewToken(token: String) {
        Timber.i("--> onNewToken: $token")

        if (token.isNotEmpty()) {
//            sendRegistrationToServer()
        }
    }

    @WorkerThread
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.e("[1] --> onMessageReceived: " + remoteMessage.notification!!.body)
        if (remoteMessage.notification!!.body != null) {
            Timber.i(TAG, "[2] --> Notification Body: " + remoteMessage.notification)
        }
    }


}