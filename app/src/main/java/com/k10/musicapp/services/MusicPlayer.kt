package com.k10.musicapp.services

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MusicPlayer : MediaPlayer() {
    companion object {

        const val NONE = 0
        const val ERROR = -1
        const val IDLE = 1
        const val INITIALIZED = 2
        const val PREPARING = 3
        const val PREPARED = 4
        const val PLAYING = 5
        const val PAUSED = 6
        const val STOPPED = 7
        const val PLAYBACK_COMPLETE = 8

        private var currentPlaybackPosition: Int =
            0 // currently in milliseconds change it to percentage
        var currentState: Int = IDLE
        private var orderedState: Int = PAUSED

    }

    init {
        setOnPreparedListener {
            currentState = PREPARED
            if (orderedState == PLAYING)
                playPlayback()
        }

        setOnErrorListener { mp, what, extra ->
            //handle error occurrence  then return true
            return@setOnErrorListener false
        }

        setOnCompletionListener {
//            onPlaybackCompletionListener?.let{
//                onPlaybackCompletionListener.playbackCompleted()
//            }
        }
    }

//    private var onPlaybackCompletionListener: PlaybackCompletionListener? = null

    fun playNewMusic(url: String, seek: Int = 0) {
        orderedState = PLAYING
        reset()
        currentState = IDLE
        CoroutineScope(IO).launch {
            setDataSource(url)
            currentState = INITIALIZED
        }
        prepareAsync()
        currentPlaybackPosition = seek
        currentState = PREPARING
    }

    fun playPlayback() {
        orderedState = PLAYING
        if (currentState >= PREPARED) {
            start()
            seekTo(currentPlaybackPosition)  //If seek is changed while in PAUSE, MediaPlayer doesn't catch seek
            currentState = PLAYING
            orderedState = NONE
        }
    }

    fun pausePlayback() {
        orderedState = PAUSED
        if (isPlaying) {
            pause()
            currentState = PAUSED
            orderedState = NONE
        }
    }

    //Move seek of currently Playing/Preparing Song
    fun moveSeekTo(position: Int = 0) {
        currentPlaybackPosition = position
    }

    fun setOnPlaybackCompleted(/*completionListener: PlaybackCompletionListener*/) {
//        onPlaybackCompletionListener = completionListener
//        setOnCompletionListener {
//            //handle Playback end
//
//        }
    }

    //This method stops playback and Release resource
    fun destroy() {
        reset()
        release()
    }
}