package com.k10.musicapp.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.k10.musicapp.R
import com.k10.musicapp.datamodel.SongObject
import com.k10.musicapp.services.PlayerService
import com.k10.musicapp.ui.splash.SplashActivity
import com.k10.musicapp.utils.Constants

class CustomNotification {

    companion object {
        private const val TAG = "NotificationBuilder"
        private const val CHANNEL_ID = "MusicApp_Channel"
        const val NOTIFICATION_ID = 1

        fun removeNotification(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.cancel(0)
        }

        fun returnNotification(
            context: Context,
            songObject: SongObject,
            packageName: String,
            showPlay: Boolean
        ): Notification {

            val playPauseIntent = Intent(context, PlayerService::class.java)
            playPauseIntent.action = Constants.PLAY_PAUSE

            val nextIntent = Intent(context, PlayerService::class.java)
            nextIntent.action = Constants.NEXT

            val previousIntent = Intent(context, PlayerService::class.java)
            previousIntent.action = Constants.PREVIOUS

            val clickIntent = Intent(context, SplashActivity::class.java)

            val p0 = PendingIntent.getService(context, 100, playPauseIntent, FLAG_UPDATE_CURRENT)
            val p1 = PendingIntent.getService(context, 101, nextIntent, FLAG_UPDATE_CURRENT)
            val p2 = PendingIntent.getService(context, 102, previousIntent, FLAG_UPDATE_CURRENT)
            val p3 = PendingIntent.getActivity(context, 103, clickIntent, FLAG_UPDATE_CURRENT)

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .addAction(R.drawable.ic_fast_rewind, "Previous", p2)

            if(showPlay){
                notificationBuilder.addAction(R.drawable.ic_play, "Play", p0)
            }else{
                notificationBuilder.addAction(R.drawable.ic_pause, "Pause", p0)
            }

            notificationBuilder.addAction(R.drawable.ic_fast_forward, "Next", p1)

            notificationBuilder
                .setShowWhen(false)
                .setContentTitle(songObject.songName)
                .setContentText(songObject.singer)
                .setContentIntent(p3)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())

            return notificationBuilder.build()
        }
    }
}