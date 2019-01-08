package com.example.sebastian.redminereader

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat

class NotificationHelper(base: Context): ContextWrapper(base) {
    val channelID = "channelID"
    val channelName = "channel"
    private var mManager: NotificationManager? = null
    init {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()
    }
    @TargetApi(Build.VERSION_CODES.O)
    fun createChannel() {
        val channel: NotificationChannel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = R.color.colorPrimary
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getManager().createNotificationChannel(channel)
    }
    fun getManager(): NotificationManager {
        if (mManager == null) {
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager!!
    }
    fun getChannelNotification(title: String, message: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, channelID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_mail_black_24dp)
    }
}