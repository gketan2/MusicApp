package com.k10.musicapp.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import com.k10.musicapp.datamodel.PlaybackObject
import com.k10.musicapp.datamodel.SongObject
import com.k10.musicapp.utils.CommandOrigin
import com.k10.musicapp.wrappers.PlaybackWrapper

/**
 * TODO HANDLE PLAYLIST
 * TODO MATCH IF ORDERED SONG IS CURRENTLY PLAYING(only NEED TO CHECK for last played from preference)
 * -->maybe match on base of songId<--
 * need to account for matching currently playing song with ordered/next song
 */
class PlayerService : Service(), PlaybackCompleteListener {

    private val TAG = "PlayerService"
    private lateinit var musicPlayer: MusicPlayer
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

    private val binder = PlayerServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        //Initiate musicPlayer Object
        musicPlayer = MusicPlayer()
        musicPlayer.setOnPlaybackCompletedListener(this)
        Log.d(TAG, "onCreate called")
    }

    override fun onDestroy() {
        super.onDestroy()
        currentSong = null
        musicPlayer.destroy()

        Log.d(TAG, "onDestroy called")
    }

    fun makeToast() {
        Toast.makeText(this, "$TAG Toast", Toast.LENGTH_SHORT).show()
    }

    fun getPlaybackLiveData(): MediatorLiveData<PlaybackWrapper<PlaybackObject>> {
        return musicPlayer.progress
    }

    fun pausePlayback() {
        musicPlayer.pausePlayback()
    }

    fun playPlayback(from: CommandOrigin) {
        when (from) {
            CommandOrigin.NOTIFICATION -> {
                //maybe easiest to handle
                //should call this from broadcast receiver
                //make Broadcast Receiver
            }
            CommandOrigin.FLOATING_BAR -> {
                //Get from preference if not currently started Playing
                //Load Playlist if present
                if (currentSong != null) {
                    musicPlayer.playPlayback()
                }else{
                    //TODO get this from SharedPreferences
                    val urlFromPreferences = "https://mp3d.jamendo.com/?trackid=799037&format=mp32"
                    //setting new currentSong
                    currentSong = SongObject(songStreamUrl = urlFromPreferences)
                    playThisUrl(urlFromPreferences)
                }
            }
            CommandOrigin.PLAYER_UI -> {
            }

            CommandOrigin.PLAYLIST_UI -> {
            }
            CommandOrigin.LIST_UI -> {
            }
        }
        if (currentSong != null)
            musicPlayer.playPlayback()
    }

    fun seekPlayback(seekTo: Int = 0) {
        musicPlayer.moveSeekTo(seekTo)
    }

    fun playThisUrl(uri: String, seek: Int = 0) {
        musicPlayer.playNewMusic(uri, seek)
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
}