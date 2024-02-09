package com.fredy.mysavings.Data.Notification

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fredy.mysavings.MainActivity
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.appIcon
import dagger.hilt.android.qualifiers.ApplicationContext


class NotificationWorker(
    @ApplicationContext private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return if (true) {
            buildAndShowDailyNotification()
            Result.success()
        } else {
            cancelNotificationById(
                NotificationCredentials.DailyNotification.NOTIFICATION_MANAGER_ID
            )
            Result.failure()
        }
    }

    private fun buildAndShowDailyNotification() {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(
            context,
            NotificationCredentials.DailyNotification.NOTIFICATION_ID
        )
            .setContentTitle(NotificationCredentials.DailyNotification.NOTIFICATION_TITLE)
            .setContentText(NotificationCredentials.DailyNotification.NOTIFICATION_CONTENT_TEXT)
            .setSmallIcon(appIcon.image)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        showNotifications(notification,NotificationCredentials.DailyNotification.NOTIFICATION_MANAGER_ID)
    }

    private fun showNotifications(notification: Notification, notificationId: Int){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(context).notify(
            notificationId,
            notification
        )
    }

    private fun cancelNotificationById(notificationId:Int){
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}
