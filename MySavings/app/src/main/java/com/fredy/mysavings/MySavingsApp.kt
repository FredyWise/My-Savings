package com.fredy.mysavings

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import com.fredy.domain.credentials.Configuration
//import com.fredy.mysavings.DI.AppModule
//import com.fredy.mysavings.DI.AppModuleImpl
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MySavingsApp: Application(){

    @Inject
    lateinit var notificationManager: NotificationManagerCompat
    override fun onCreate() {
        super.onCreate()
        notificationManager
        if(Configuration.Debug.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}