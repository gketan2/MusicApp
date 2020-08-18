package com.k10.musicapp.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.k10.musicapp.datamodel.SongObject

class PlayerService : Service() {

    companion object {
        private const val TAG = "PlayerService"
        private val musicPlayer: MusicPlayer = MusicPlayer()
        private var currentSong: SongObject? = null
    }

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
        Log.d(TAG, "onCreate called")

        //TODO get last playing song from SharedPreference
        //return
        //playThisMusic
//        playThisSong(music_url)
        //mediaPlayer.currentPosition

    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer.destroy()
        stopSelf()

        Log.d(TAG, "onDestroy called")
    }

    fun makeToast(){
        Toast.makeText(this, "Caled From Activity", Toast.LENGTH_SHORT).show()
    }

    fun pausePlayback(){
        musicPlayer.pausePlayback()
    }

    fun playPlayback(){
        musicPlayer.playPlayback()
    }

    fun seekPlayback(seekTo: Int = 0){
        musicPlayer.moveSeekTo(seekTo)
    }

    fun playThisSong(uri: String) {
        musicPlayer.playNewMusic(uri)
    }
//
//    private fun playThisSong(songObject: SongObject) {
//        mediaPlayer.reset()
//        //Setting the Song about to be played to the repository.
//        SongStateRepository.getInstance().setCurrentSong(songObject)
//
//        CoroutineScope(IO).launch {
//            mediaPlayer.setDataSource(songObject.songStreamUrl)
//            mediaPlayer.prepare()
//            withContext(Main) {
//                songObject.songLength = mediaPlayer.duration
//                mediaPlayer.start()
//            }
//        }
//    }
}