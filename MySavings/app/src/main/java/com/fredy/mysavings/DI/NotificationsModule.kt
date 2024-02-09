package com.fredy.mysavings.DI

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.fredy.mysavings.Data.Notification.NotificationCredentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {

    @Provides
    @Singleton
    fun provideNotificationManagerCompat(@ApplicationContext context: Context): NotificationManagerCompat {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        val dailyNotificationChannel = dailyNotificationChannel()
        notificationManagerCompat.createNotificationChannel(dailyNotificationChannel)
        return notificationManagerCompat
    }

    private fun dailyNotificationChannel(): NotificationChannel {
        val channel = NotificationChannel(
            NotificationCredentials.DailyNotification.NOTIFICATION_ID,
            NotificationCredentials.DailyNotification.NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = NotificationCredentials.DailyNotification.NOTIFICATION_DESCRIPTION
        }
        return channel
    }
}
