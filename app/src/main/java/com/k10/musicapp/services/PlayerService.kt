package com.k10.musicapp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MediatorLiveData
import com.k10.musicapp.datamodel.PlaybackObject
import com.k10.musicapp.datamodel.SongObject
import com.k10.musicapp.notification.CustomNotification
import com.k10.musicapp.storge.Preference
import com.k10.musicapp.utils.CommandOrigin
import com.k10.musicapp.utils.Constants
import com.k10.musicapp.utils.PlayerRequestType
import com.k10.musicapp.wrappers.PlaybackWrapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * TODO HANDLE PLAYLIST
 * TODO MATCH IF ORDERED SONG IS CURRENTLY PLAYING(only NEED TO CHECK for last played from preference)
 * -->maybe match on base of songId<--
 * need to account for matching currently playing song with ordered/next song
 */
@AndroidEntryPoint
class PlayerService : Service(), MusicPlayerListener, AudioManager.OnAudioFocusChangeListener {

    private val TAG = "PlayerService"
    private var musicPlayer: MusicPlayer? = null
    private var currentSong: SongObject? = null
    private var currentSongLiveData: MediatorLiveData<SongObject> = MediatorLiveData()

    private var currentPlaylist: ArrayList<SongObject>? = ArrayList()
    private var currentPlayingIndex: Int = 0

    private var onRepeat: Boolean = false

    private var audioManager: AudioManager? = null

    private var telephonyManager: TelephonyManager? = null

    @Inject
    lateinit var preference: Preference

    /**
     * General Binder Class to contact from a view component.
     */
    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService {
            return this@PlayerService
        }
    }

    private val binder = PlayerServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            Log.d(TAG, "onStartCommand: intent: ${it.action}")
            when (it.action) {
                Constants.PLAY_PAUSE -> {
                    requestPlayerService(PlayerRequestType.PLAYPAUSE, CommandOrigin.NOTIFICATION)
                }
                Constants.NEXT -> {
                    requestPlayerService(PlayerRequestType.NEXT, CommandOrigin.NOTIFICATION)
                }
                Constants.PREVIOUS -> {
                    requestPlayerService(PlayerRequestType.PREVIOUS, CommandOrigin.NOTIFICATION)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        //Initiate musicPlayer Object
        musicPlayer?.destroy()
        musicPlayer = null
        musicPlayer = MusicPlayer()
        musicPlayer?.setOnMusicPlayerListener(this)

        currentSong = preference.getLastPlayingSong()
        if (currentSong != null) {
            Log.d(TAG, "onCreate: got song from preference")
            currentSongLiveData.value = currentSong!!
            playThisSong(currentSong!!)
            pausePlayback()
        }

        registerPhoneStateListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        //storing last played in preference
        if (currentSong != null) {
            CoroutineScope(Dispatchers.Default).launch {
                preference.setLastPlaying(currentSong!!)
                currentSong = null
            }
        }
        musicPlayer?.destroy()
        removeAudioFocus()
        removeTelephonyListener()
    }

    fun getCurrentSongObject(): MediatorLiveData<SongObject> {
        return currentSongLiveData
    }

    fun getCurrentSeekLiveData(): MediatorLiveData<PlaybackObject> {
        return musicPlayer?.progress!!
    }

    fun getPlayerStateLiveData(): MediatorLiveData<PlaybackWrapper<PlaybackObject>> {
        return musicPlayer?.playerState!!
    }

    /**
     * Handle(entry point) any music player related commands like play, pause, next, previous
     *
     * @param what what type of function need to be run.
     * This parameter can only be from {@link PlayerRequestState}
     * @param from from where this is called, for e.g. Notification bar or player ui or clicked on list of song.
     * See {@link CommandOrigin}
     */
    fun requestPlayerService(what: PlayerRequestType, from: CommandOrigin) {
        Log.d(TAG, "requestPlayerService: $what  $from")
        when (from) {
            CommandOrigin.FLOATING_BAR -> {
                if (what == PlayerRequestType.PLAYPAUSE)
                    playPausePlayback()
            }
            CommandOrigin.PLAYER_UI -> {
                when (what) {
                    PlayerRequestType.PLAYPAUSE -> playPausePlayback()
                    PlayerRequestType.NEXT -> nextPlayback()
                    PlayerRequestType.PREVIOUS -> previousPlayback()
                    else -> {
                    }
                }
            }
            CommandOrigin.NOTIFICATION -> {
                when (what) {
                    PlayerRequestType.PLAYPAUSE -> playPausePlayback()
                    PlayerRequestType.NEXT -> nextPlayback()
                    PlayerRequestType.PREVIOUS -> previousPlayback()
                    else -> {
                    }
                }
            }
            CommandOrigin.LIST_UI -> {
            }
            CommandOrigin.PLAYLIST_UI -> {
            }
        }
    }

    private fun playPausePlayback() {
        if (currentSong != null) {
            if (musicPlayer?.currentState!! == PlayerState.PLAYING
                || musicPlayer?.orderedState == PlayerState.PLAYING) {
                pausePlayback()
            } else if (musicPlayer?.currentState!! == PlayerState.PAUSED
                || musicPlayer?.orderedState == PlayerState.PAUSED
                || musicPlayer?.currentState!! < PlayerState.PREPARING
            ) {
                playPlayback()
            }
        } else {
            Log.d(TAG, "playPausePlayback: creating fake object since null from preference")
            //TODO REMOVE THIS WHEN (can play from other Sources, like a list of song or search result) IMPLEMENTED
            val urlFromPreferences = "https://mp3d.jamendo.com/?trackid=799037&format=mp32"
            //setting new currentSong
            currentSong = SongObject(songStreamUrl = urlFromPreferences)
            currentSongLiveData.value = currentSong!!
            playThisSong(currentSong!!)
        }
    }

    /**
     * Play the mediaPLayer,
     * doesnt care about the current state of the MusicPlayer/MediaPlayer
     */
    private fun playPlayback() {
        if (requestAudioFocus()) {
            musicPlayer?.playPlayback()
            //show notification
            updateNotification(false)
        } else {
            Log.d(TAG, "playPlayback: audioRequestFailed")
        }
    }

    /**
     * Pause the mediaPLayer,
     * doesnt care about the current state of the MusicPlayer/MediaPlayer
     * (therefore may produce error)
     * */
    private fun pausePlayback() {
        musicPlayer?.pausePlayback()
        updateNotification(true)
        stopForeground(false)
        if (currentSong != null) {
            currentSong?.storedPlaybackSeek = musicPlayer?.getCurrentPlaybackPosition()!!
            CoroutineScope(Dispatchers.Default).launch {
                preference.setLastPlaying(currentSong!!)
            }
        }
    }

    private fun nextPlayback() {
        //maybe
        //TODO check when implementing this
        onPlaybackComplete()
    }

    private fun previousPlayback() {
        //TODO("Not Implemented Yet")
    }

    /**
     * provide seek  in the rage  0 - 1000, like perThousand
     */
    fun seekPlaybackTo(seekTo: Int = 0) {
        musicPlayer?.moveSeekTo(seekTo)
    }

    /**
     * wrapper of playThisUrl(uri, seek), just to store song in lastPlaying in sharedPreference
     */
    private fun playThisSong(songObject: SongObject) {
        //Storing current playing song to lastPlaying song in preference
        CoroutineScope(Dispatchers.Default).launch {
            preference.setLastPlaying(currentSong!!)
        }
        playThisUrl(songObject.songStreamUrl, songObject.storedPlaybackSeek)
    }

    /**
     * Should only be called from playThisSong(SongObject)
     */
    private fun playThisUrl(uri: String, seek: Int = 0) {
        requestAudioFocus()
        musicPlayer?.playNewMusic(uri, seek)
        if (currentSong != null)
            updateNotification(false)
    }

    override fun onPlaybackComplete() {
        //Stops if no song is in the list,
        //then the playback stops.
        if (currentPlaylist != null) {
            if (currentPlaylist?.size!! > 0) {
                currentPlayingIndex = (currentPlayingIndex + 1) % currentPlaylist?.size!!
                currentSong = currentPlaylist!![currentPlayingIndex]
                currentSongLiveData.value = currentSong!!
                playThisSong(currentSong!!)
            } else {
                updateNotification(true)
                stopForeground(false)
            }
        } else {
            updateNotification(true)
            stopForeground(false)
        }
    }

    override fun onPlaybackError() {
        currentSong = null
        CustomNotification.removeNotification(applicationContext)
    }

    private fun updateNotification(showPlay: Boolean) {
        if (currentSong == null) {
            Log.d(TAG, "updateNotification: Could not update notification")
            return
        }

        Log.d(TAG, "updateNotification: updating Notification")
        startForeground(
            CustomNotification.NOTIFICATION_ID,
            CustomNotification.returnNotification(
                applicationContext,
                currentSong!!,
                showPlay
            )
        )
    }

    /**
     * Listen if other app tries to play music,
     * then pause the music playback.
     */
    override fun onAudioFocusChange(focusChange: Int) {
        Log.d(TAG, "onAudioFocusChange: $focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (musicPlayer?.isPlaying!! || musicPlayer?.currentState!! <= PlayerState.PREPARING) {
                    playPlayback()
                    musicPlayer?.setVolume(0.2f, 0.2f)
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                pausePlayback()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pausePlayback()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                musicPlayer?.setVolume(0.1f, 0.1f)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val audioFocusRequest: AudioFocusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            ).setOnAudioFocusChangeListener(this)
            .build()

    /**
     * If any other app is playing audio it will be paused.
     * return true, if other app is paused, now play your audio,
     * if returned false, other app is not leaving focus, do not play
     */
    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return audioManager?.requestAudioFocus(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return audioManager?.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
        Log.d(TAG, "requestAudioFocus: not returned")
        return true
    }

    private fun removeAudioFocus(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                    audioManager?.abandonAudioFocusRequest(audioFocusRequest)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            audioManager?.abandonAudioFocus(this)
        }

        Log.d(TAG, "removeAudioFocus: not returned")
        return true
    }

    /**
     * Listener for Phone Call
     */
    private val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    if (musicPlayer?.currentState!! > PlayerState.PREPARING)
                        pausePlayback()
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                }
            }
        }
    }

    private fun registerPhoneStateListener() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun removeTelephonyListener() {
        telephonyManager = null
    }
}