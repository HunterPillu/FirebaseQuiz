package com.prinkal.quiz.ui.firebase

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.prinkal.quiz.R
import com.prinkal.quiz.ui.main.view.HomeActivity
import com.prinkal.quiz.utils.Config
import com.prinkal.quiz.utils.CustomLog


class FCMService : FirebaseMessagingService() {

    private val TAG = FCMService::class.java.name

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        CustomLog.d(TAG, "From: ${remoteMessage.from}")

        if (!isAppIsInBackground()) {
            return
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            CustomLog.d(TAG, "Message data payload: " + remoteMessage.data)

            try {
                val callingFrom: String? = remoteMessage.data["callerId"]
                CustomLog.d(TAG, callingFrom)
                sendNotification(
                    remoteMessage.data["title"],
                    remoteMessage.data["body"],
                    remoteMessage.data
                )

                /*val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                remoteMessage.data.keys.forEach { key ->
                    intent.putExtra(key, remoteMessage.data[key])
                }
                startActivity(intent)*/
            } catch (e: Exception) {
                e.printStackTrace()
                CustomLog.e(TAG, e)
            }

        }

        // Check if message contains a notification payload.
        /*remoteMessage.notification?.let {
            CustomLog.d(TAG, "Message Notification Body: ${it.body}")
            it.body?.let { msg ->
                //check the notification for data
                //sendNotification(it.title, msg, remoteMessage.data)
            }
        }*/
    }

    override fun onNewToken(token: String) {
        CustomLog.d(TAG, "Refreshed token: $token")
        //FirebaseData.updateToken()
    }

    private fun sendNotification(
        title: String?,
        messageBody: String?,
        notifData: Map<String, String>
    ) {

        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        notifData.keys.forEach { key ->
            intent.putExtra(key, notifData[key])
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val defaultChannelId = Config.PUSH_NOTIFICATION_CHANNEL_NAME

        val notificationBuilder = NotificationCompat.Builder(this, defaultChannelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //should create channels as needed.
            //e.g next time got channel X? need to create here too
            val channel = NotificationChannel(
                defaultChannelId,
                Config.PUSH_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
            Config.PUSH_NOTIFICATION_ID,
            notificationBuilder.build()
        )
    }


    private fun isAppIsInBackground(): Boolean {
        val context = applicationContext
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        isInBackground = false
                    }
                }
            }
        }
        return isInBackground
    }
}
