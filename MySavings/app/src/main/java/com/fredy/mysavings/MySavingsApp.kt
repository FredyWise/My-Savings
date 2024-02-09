package com.fredy.mysavings

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.fredy.mysavings.Data.Notification.NotificationCredentials
//import com.fredy.mysavings.DI.AppModule
//import com.fredy.mysavings.DI.AppModuleImpl
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MySavingsApp: Application(){

    @Inject
    lateinit var notificationManager: NotificationManagerCompat
    override fun onCreate() {
        super.onCreate()
        notificationManager
    }
}