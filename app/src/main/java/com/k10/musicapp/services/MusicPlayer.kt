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
    var currentState: Int = PlayerState.IDLE
    private var orderedState: Int = PlayerState.PAUSED

    //track how much it is played
    val progress: MediatorLiveData<PlaybackObject> = MediatorLiveData()

    //state of the player
    val playerState: MediatorLiveData<PlaybackWrapper<PlaybackObject>> = MediatorLiveData()

    init {
        setOnPreparedListener {
            Log.d(TAG, "prepared Listener")
            currentState = PlayerState.PREPARED
            if (orderedState == PlayerState.PLAYING)
                playPlayback()
            //No need to pause if it is not even started
            //it will cause error if you do
            //else if (orderedState == PlayerState.PAUSED)
            //pausePlayback()
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
            if (error.isBlank())
                error = "$what : $extra"
            onMusicPlayerListener?.onPlaybackError()
            playerState.value = PlaybackWrapper.error(error)
            reset()
            return@setOnErrorListener true
        }

        setOnCompletionListener {
            Log.d(TAG, "playback completed")
            currentState = PlayerState.PLAYBACK_COMPLETE
            playerState.value = PlaybackWrapper.complete()
            onMusicPlayerListener?.onPlaybackComplete()
        }
    }

    /**
     * Interface variable to detect playback completed
     */
    private var onMusicPlayerListener: MusicPlayerListener? = null

    /**
     * Set playbackCompletionListener to get notified when playback completed
     */
    fun setOnMusicPlayerListener(musicPlayerListener: MusicPlayerListener) {
        onMusicPlayerListener = musicPlayerListener
    }

    /**
     * nulls the listener interface variable
     */
    private fun removeOnPlaybackCompletedListener() {
        onMusicPlayerListener = null
    }

    /**
     * Reset the player and starts given playback url
     * */
    fun playNewMusic(url: String, seek: Int = 0) {
        orderedState = PlayerState.PLAYING
        reset()
        CoroutineScope(IO).launch {
            setDataSource(url)
            currentState = PlayerState.INITIALIZED
            prepareAsync()
        }
        currentPlaybackPosition = seek
        currentState = PlayerState.PREPARING
        //Updating Live data about state
        playerState.value = PlaybackWrapper.loading()
    }

    /**
     * Plays the playback if MediaPlayer is Prepared
     * Resume if is in pause state
     */
    fun playPlayback() {
        orderedState = PlayerState.PLAYING
        if (currentState == PlayerState.PAUSED
            || currentState == PlayerState.PREPARED
            || currentState == PlayerState.PLAYBACK_COMPLETE
        ) {
            start()
            //Setting playerState to playing
            playerState.value = PlaybackWrapper.playing()
            //If seek is changed while in PAUSE, MediaPlayer doesn't catch seek
            if(currentState == PlayerState.PLAYBACK_COMPLETE)
                currentPlaybackPosition = 0
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
        playerState.value = PlaybackWrapper.paused()
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
                    PlaybackObject(
                        currentPosition,
                        duration,
                        currentPosition / 60000,
                        (currentPosition / 1000) % 60,
                        duration / 60000,
                        (duration / 1000) % 60
                    )
                )
                delay(50)
            }
        }
    }

    /**
     * Move seek of currently Playing/Preparing Song.
     * Provide in the range of 0 - 1000, like perThousand
     */
    fun moveSeekTo(position: Int) {
        if (currentState >= PlayerState.PREPARED) {
            currentPlaybackPosition = thousandToDuration(position)
            if (isPlaying)
                super.seekTo(currentPlaybackPosition)
            else if (currentState == PlayerState.PLAYBACK_COMPLETE)
                playPlayback()
        }else{
            currentPlaybackPosition = position
        }
    }

    private fun thousandToDuration(position: Int): Int {
        if (position !in 0..1000) {
            return 0
        }
        return position * (duration / 1000)
    }

    /**
     * This method stops playback and Release resource
     * by calling MediaPlayer.reset() and MediaPlayer.release()
     * also removes playbackCompleted listener
     */
    fun destroy() {
        playerState.value = PlaybackWrapper.none()
        removeOnPlaybackCompletedListener()
        reset()
        super.release()
    }

    /**
     * Reset the MediaPlayer and set songLength = 0
     * Set currentState to PlayerState.IDLE
     */
    override fun reset() {
        //Reset 0:00,0:00 to current seek
        progress.value = PlaybackObject(
            0,
            0,
            0,
            0
        )
        if (currentState >= PlayerState.PREPARED) {
            super.stop()
        }
        super.reset()
        currentState = PlayerState.IDLE
        playerState.value = PlaybackWrapper.idle()
        currentPlaybackPosition = 0
    }
}

interface MusicPlayerListener {
    fun onPlaybackComplete()
    fun onPlaybackError()
}

/**
 * Possible States in which MusicPlayer can be
 */
class PlayerState {
    companion object {
        const val NONE = 0
        const val ERROR = 1
        const val IDLE = 2
        const val STOPPED = 3
        const val INITIALIZED = 4
        const val PREPARING = 5
        const val PREPARED = 6
        const val PLAYING = 7
        const val PAUSED = 8
        const val PLAYBACK_COMPLETE = 9
    }
}