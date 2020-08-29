package com.k10.musicapp.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE

/**
 * Currently not using BroadcastReceiver.
 * Directly calling service from Notification via PendingIntent
 */
class CustomBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "CustomBroadcastReceiver"
        private const val PLAY_PAUSE = "com.k10.musicapp.PLAY_PAUSE"
        private const val NEXT = "com.k10.musicapp.NEXT"
        private const val PREVIOUS = "com.k10.musicapp.PREVIOUS"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ${intent?.action}")
        intent?.let {
            when(it.action){
                PLAY_PAUSE -> {

                }
                NEXT -> {}
                PREVIOUS -> {}
                ACTION_MEDIA_BUTTON -> {
                    onMediaButtonPressed(it.getParcelableExtra(EXTRA_KEY_EVENT) as KeyEvent)
                }
                ACTION_HEADSET_PLUG -> {}
            }
        }
    }

    private fun onMediaButtonPressed(event: KeyEvent){
        Log.d(TAG, event.toString())
        when(event.keyCode){
            KEYCODE_MEDIA_PLAY_PAUSE -> {
                Log.d(TAG, "Pressed")
            }
        }
    }
}