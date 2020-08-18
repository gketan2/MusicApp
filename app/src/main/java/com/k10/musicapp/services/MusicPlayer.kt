package com.k10.musicapp.services

import android.media.MediaPlayer
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MusicPlayer : MediaPlayer() {
    companion object {
        // currently in milliseconds change it to percentage
        private var currentPlaybackPosition: Int = 0
        var currentState: PlayerState = PlayerState.IDLE
        private var orderedState: PlayerState = PlayerState.PAUSED
        val songLength: MediatorLiveData<Int> = MediatorLiveData()
    }

    init {
        setOnPreparedListener {
            currentState = PlayerState.PREPARED
            songLength.value = duration
            if (orderedState == PlayerState.PLAYING)
                playPlayback()
            else if(orderedState == PlayerState.PAUSED)
                pause()
        }

        setOnErrorListener { mp, what, extra ->
            //handle error occurrence  then return true
            reset()
            return@setOnErrorListener false
        }

        setOnCompletionListener {
//            onPlaybackCompletionListener?.let{
//                onPlaybackCompletionListener.playbackCompleted()
//            }
        }
    }

//    private var onPlaybackCompletionListener: PlaybackCompletionListener? = null

    /**
     * Reset the player and starts given playback url
     * */
    fun playNewMusic(url: String, seek: Int = 0) {
        orderedState = PlayerState.PLAYING
        reset()
        currentState = PlayerState.IDLE
        CoroutineScope(IO).launch {
            setDataSource(url)
            currentState = PlayerState.INITIALIZED
            prepareAsync()
        }
        currentPlaybackPosition = seek
        currentState = PlayerState.PREPARING
    }

    /**
     * Plays the playback if MediaPlayer is Prepared
     * Resume if is in pause state
     */
    fun playPlayback() {
        orderedState = PlayerState.PLAYING
        if (currentState == PlayerState.PAUSED || currentState == PlayerState.PREPARED) {
            start()
            seekTo(currentPlaybackPosition)  //If seek is changed while in PAUSE, MediaPlayer doesn't catch seek
            currentState = PlayerState.PLAYING
            orderedState = PlayerState.NONE
        }
    }

    /**
     * Pause the playback if playing,
     * If MediaPlayer is not Playing(or is in any other state)
     * will pause after that state is completed,
     * i.e. will not play the playback
     */
    fun pausePlayback() {
        orderedState = PlayerState.PAUSED
        if (isPlaying) {
            pause()
            currentState = PlayerState.PAUSED
            orderedState = PlayerState.NONE
        }
    }

    /**
     * Move seek of currently Playing/Preparing Song
     */
    fun moveSeekTo(position: Int = 0) {
        currentPlaybackPosition = position
        if(isPlaying)
            seekTo(position)
    }

    fun setOnPlaybackCompleted(/*completionListener: PlaybackCompletionListener*/) {
//        onPlaybackCompletionListener = completionListener
//        setOnCompletionListener {
//            //handle Playback end
//
//        }
    }

    /**
     * This method stops playback and Release resource
     * by calling MediaPlayer.reset() and MediaPlayer.release()
     */
    fun destroy() {
        reset()
        super.release()
    }

    /**
     * Reset the MediaPlayer and set songLength = 0
     * Set currentState to PlayerState.IDLE
     */
    override fun reset() {
        super.stop()
        super.reset()
        currentState = PlayerState.IDLE
        songLength.value = 0
    }
}
/**
 * Possible States in which MusicPlayer can be
 */
enum class PlayerState {
    NONE,
    ERROR,
    IDLE,
    INITIALIZED,
    PREPARING,
    PREPARED,
    PLAYING,
    PAUSED,
    STOPPED,
    PLAYBACK_COMPLETE
}