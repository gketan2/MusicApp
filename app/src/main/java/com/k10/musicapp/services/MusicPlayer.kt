package com.k10.musicapp.services

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import com.k10.musicapp.datamodel.PlaybackObject
import com.k10.musicapp.wrappers.PlaybackWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayer : MediaPlayer() {
    private val TAG = "MusicPlayer"

    //currently in milliseconds change it to percentage
    private var currentPlaybackPosition: Int = 0

    //currently playing url
    private var currentUrl: String? = null

    //variables to help in state transition
    private var currentState: PlayerState = PlayerState.IDLE
    private var orderedState: PlayerState = PlayerState.PAUSED

    //track how much it is played
    val progress: MediatorLiveData<PlaybackWrapper<PlaybackObject>> = MediatorLiveData()

    init {
        setOnPreparedListener {
            Log.d(TAG, "prepared Listener")
            currentState = PlayerState.PREPARED
            if (orderedState == PlayerState.PLAYING)
                playPlayback()
            else if (orderedState == PlayerState.PAUSED)
                pausePlayback()
        }

        setOnErrorListener { _, what, extra ->
            Log.d(TAG, "error listener")
            //handle error occurrence then return true
            var error = ""
            when (what) {
                MEDIA_ERROR_UNKNOWN -> error = "Something Went Wrong..."
                MEDIA_ERROR_SERVER_DIED -> error = "Sorry, we are having problems with our servers"
            }
            when (extra) {
                MEDIA_ERROR_IO -> error = "We think it's your internet connection"
                MEDIA_ERROR_MALFORMED -> error = "This file maybe is not for playing"
                MEDIA_ERROR_UNSUPPORTED -> error = "Sorry, Your phone cannot play that song"
                MEDIA_ERROR_TIMED_OUT -> error = "We think it's your internet connection"
            }
            progress.value = PlaybackWrapper.error(error)
            reset()
            return@setOnErrorListener true
        }

        setOnCompletionListener {
            currentState = PlayerState.PLAYBACK_COMPLETE
            onPlaybackCompletionListener?.onPlaybackComplete()
        }
    }

    /**
     * Interface variable to detect playback completed
     */
    private var onPlaybackCompletionListener: PlaybackCompleteListener? = null

    /**
     * Set playbackCompletionListener to get notified when playback completed
     */
    fun setOnPlaybackCompletedListener(completionListener: PlaybackCompleteListener) {
        onPlaybackCompletionListener = completionListener
    }

    /**
     * nulls the listener interface variable
     */
    private fun removeOnPlaybackCompletedListener() {
        onPlaybackCompletionListener = null
    }

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
        //Updating Live data about state
        progress.value = PlaybackWrapper.loading()
    }

    /**
     * Plays the playback if MediaPlayer is Prepared
     * Resume if is in pause state
     */
    fun playPlayback() {
        orderedState = PlayerState.PLAYING
        if (currentState == PlayerState.PAUSED || currentState == PlayerState.PREPARED) {
            start()
            //If seek is changed while in PAUSE, MediaPlayer doesn't catch seek
            seekTo(currentPlaybackPosition)
            playbackUpdate()
            currentState = PlayerState.PLAYING
            orderedState = PlayerState.NONE
        } else if (currentState == PlayerState.PLAYING || isPlaying) {
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
        if (currentState <= PlayerState.PREPARING)
            return

        super.pause()
        //Updating LiveData about the currentState
        progress.value = PlaybackWrapper.paused()
        currentState = PlayerState.PAUSED
        orderedState = PlayerState.NONE
    }

    /**
     * Updates Live Data with latest playback positions
     * and update local variable "currentPlaybackPosition" with latest seek position
     */
    private fun playbackUpdate() {
        CoroutineScope(Default).launch {
            while (isPlaying) {
                currentPlaybackPosition = currentPosition
                progress.postValue(
                    PlaybackWrapper.info(
                        PlaybackObject(
                            currentPosition,
                            duration,
                            currentPosition / 60000,
                            (currentPosition / 1000) % 60,
                            duration / 60000,
                            (duration / 1000) % 60
                        ),
                        PlayerState.PLAYING
                    )
                )
                delay(50)
            }
        }
    }

    /**
     * Move seek of currently Playing/Preparing Song
     */
    fun moveSeekTo(position: Int = 0) {
        currentPlaybackPosition = position
        if (isPlaying)
            seekTo(position)
    }

    /**
     * This method stops playback and Release resource
     * by calling MediaPlayer.reset() and MediaPlayer.release()
     * also removes playbackCompleted listener
     */
    fun destroy() {
        progress.value = PlaybackWrapper.none()
        removeOnPlaybackCompletedListener()
        reset()
        super.release()
    }

    /**
     * Reset the MediaPlayer and set songLength = 0
     * Set currentState to PlayerState.IDLE
     */
    override fun reset() {
        if (currentState >= PlayerState.PREPARED) {
            super.stop()
        }
        super.reset()
        currentState = PlayerState.IDLE
        progress.value = PlaybackWrapper.idle()
    }
}

interface PlaybackCompleteListener {
    fun onPlaybackComplete()
}

/**
 * Possible States in which MusicPlayer can be
 */
enum class PlayerState {
    NONE,
    ERROR,
    IDLE,
    STOPPED,
    INITIALIZED,
    PREPARING,
    PREPARED,
    PLAYING,
    PAUSED,
    PLAYBACK_COMPLETE
}