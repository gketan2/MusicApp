package com.k10.musicapp.utils

class Constants {
    companion object{
        /**
         * Following are the action used in PendingIntent launched byNotification
         * TODO Should name them differently
         */
        const val PLAY_PAUSE = "com.k10.musicapp.PLAY_PAUSE"
        const val NEXT = "com.k10.musicapp.NEXT"
        const val PREVIOUS = "com.k10.musicapp.PREVIOUS"

        const val THEME_LIGHT = 0
        const val THEME_DARK = 1

        const val JAMENDO_CLIENT_ID = "d78b2470"
        const val JAMENDO_CLIENT_SECRET = "b3175b9d0cb0b33e957c0f2d33e55dbd"
    }
}