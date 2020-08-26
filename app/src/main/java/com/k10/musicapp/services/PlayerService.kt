package com.k10.musicapp.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import com.k10.musicapp.datamodel.PlaybackObject
import com.k10.musicapp.datamodel.SongObject
import com.k10.musicapp.utils.CommandOrigin
import com.k10.musicapp.utils.PlayerRequestType
import com.k10.musicapp.wrappers.PlaybackWrapper

/**
 * TODO HANDLE PLAYLIST
 * TODO MATCH IF ORDERED SONG IS CURRENTLY PLAYING(only NEED TO CHECK for last played from preference)
 * -->maybe match on base of songId<--
 * need to account for matching currently playing song with ordered/next song
 */
class PlayerService : Service(), MusicPlayerListener {

    private val TAG = "PlayerService"
    private var musicPlayer: MusicPlayer? = null
    private var currentSong: SongObject? = null
    private var currentPlaylist: List<SongObject>? = null
    private var currentPlayingIndex: Int = 0

    private var onRepeat: Boolean = false

    /**
     * General Binder Class to contact from a view component.
     */
    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService {
            return this@PlayerService
        }
    }

    /**
     * Broadcast Receiver for notification Buttons
     */
//    inner class NotificationBroadCastReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            intent?.let {
//                val action = it.action
//                if (action.equals(BroadcastIntents.PLAY_PAUSE, true)) {
//                    requestPlayerService(PlayerRequestType.PLAYPAUSE, CommandOrigin.NOTIFICATION)
//                } else if (action.equals(BroadcastIntents.NEXT, true)) {
//                    requestPlayerService(PlayerRequestType.NEXT, CommandOrigin.NOTIFICATION)
//                } else if (action.equals(BroadcastIntents.PREVIOUS, true)) {
//                    requestPlayerService(PlayerRequestType.PREVIOUS, CommandOrigin.NOTIFICATION)
//                }
//            }
//        }
//    }

    private val binder = PlayerServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        //Initiate musicPlayer Object
        musicPlayer?.destroy()
        musicPlayer = null
        musicPlayer = MusicPlayer()
        musicPlayer?.setOnMusicPlayerListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentSong = null
        musicPlayer?.destroy()
    }

    fun makeToast(msg: String = "") {
        Toast.makeText(this, "$TAG $msg", Toast.LENGTH_SHORT).show()
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
        when (from) {
            CommandOrigin.FLOATING_BAR -> {
            }
            CommandOrigin.PLAYER_UI -> {
                if (what == PlayerRequestType.PLAYPAUSE) {
                    playPausePlayback()
                } else if (what == PlayerRequestType.NEXT) {
                    nextPlayback()
                } else if (what == PlayerRequestType.PREVIOUS) {
                    previousPlayback()
                }
            }
            CommandOrigin.NOTIFICATION -> {
            }
            CommandOrigin.LIST_UI -> {
            }
            CommandOrigin.PLAYLIST_UI -> {
            }
        }
    }

    private fun playPausePlayback() {
        if (currentSong != null) {
            if (musicPlayer?.isPlaying!!) {
                musicPlayer?.pausePlayback()
            } else {
                musicPlayer?.playPlayback()
            }
        } else {
            //TODO get this from SharedPreferences
            val urlFromPreferences = "https://mp3d.jamendo.com/?trackid=799037&format=mp32"
            //setting new currentSong
            currentSong = SongObject(songStreamUrl = urlFromPreferences)
            playThisUrl(urlFromPreferences)
        }
    }

    private fun nextPlayback() {
        //maybe
        //TODO check when implementing this
        onPlaybackComplete()
    }

    private fun previousPlayback() {
        TODO("Not Implemented Yet")
    }

//    fun playPlayback(from: CommandOrigin) {
//        when (from) {
//            CommandOrigin.NOTIFICATION -> {
//                //maybe easiest to handle
//                //should call this from broadcast receiver
//                //make Broadcast Receiver
//            }
//            CommandOrigin.FLOATING_BAR -> {
//                //Get from preference if not currently started Playing
//                //Load Playlist if present
//                if (currentSong != null) {
//                    musicPlayer?.playPlayback()
//                } else {
//                    //TODO get this from SharedPreferences
//                    val urlFromPreferences = "https://mp3d.jamendo.com/?trackid=799037&format=mp32"
//                    //setting new currentSong
//                    currentSong = SongObject(songStreamUrl = urlFromPreferences)
//                    playThisUrl(urlFromPreferences)
//                }
//            }
//            CommandOrigin.PLAYER_UI -> {
//                //currently same as floating_bar will change after when required
//                //maybe it will be same as floating_bar we will know as we proceed
//                if (currentSong != null) {
//                    musicPlayer?.playPlayback()
//                } else {
//                    //TODO get this from SharedPreferences
//                    val urlFromPreferences = "https://mp3d.jamendo.com/?trackid=799037&format=mp32"
//                    //setting new currentSong
//                    currentSong = SongObject(songStreamUrl = urlFromPreferences)
//                    playThisUrl(urlFromPreferences)
//                }
//            }
//
//            CommandOrigin.PLAYLIST_UI -> {
//            }
//            CommandOrigin.LIST_UI -> {
//            }
//        }
//        if (currentSong != null)
//            musicPlayer?.playPlayback()
//    }

    /**
     * provide seek  in the rage  0 - 1000, like perThousand
     */
    fun seekPlaybackTo(seekTo: Int = 0) {
        musicPlayer?.moveSeekTo(seekTo)
    }

    fun playThisUrl(uri: String, seek: Int = 0) {
        musicPlayer?.playNewMusic(uri, seek)
        //TODO store this song in Preference
    }

    override fun onPlaybackComplete() {
        //TODO Play next song
        //maybe something like this

//        if (currentPlaylist != null) {
//            if (currentPlaylist!!.size > currentSong!!.songIndexInList + 1) {
//                currentSong = currentPlaylist!![currentSong!!.songIndexInList + 1]
//                playThisSong(currentSong!!.songStreamUrl)
//            } else if (onRepeat) {
//                currentSong = currentPlaylist!![0]
//                playThisSong(currentSong!!.songStreamUrl)
//            }
//        }
    }

    override fun onPlaybackError() {
        currentSong = null
    }
}