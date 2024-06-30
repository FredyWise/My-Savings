package com.fredy.mysavings.Feature.Domain.Notification

sealed class NotificationCredentials{
    object DailyNotification{
        const val NOTIFICATION_ID = "Daily Reminder"
        const val NOTIFICATION_MANAGER_ID = 1
        const val NOTIFICATION_NAME = "Daily Reminder"
        const val NOTIFICATION_DESCRIPTION = "Description of Daily Reminder"
        const val NOTIFICATION_TITLE = "Title of Daily Reminder"
        const val NOTIFICATION_CONTENT_TEXT = "content of Daily Reminder"
    }
}