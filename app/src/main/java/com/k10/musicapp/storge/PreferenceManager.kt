package com.k10.musicapp.storge

import android.content.SharedPreferences
import com.k10.musicapp.datamodel.SongObject
import com.k10.musicapp.di.LastPlayedSong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @LastPlayedSong private val lastPlayedPreferences: SharedPreferences,
    @LastPlayedSong private val editPlayedPreferences: SharedPreferences.Editor
) {

    companion object{
        private const val EXIST = "exist"
        private const val SONG_ID = "song_id"
        private const val SONG_NAME = "song_name"
        private const val SINGER_NAME = "singer_name"
        private const val SEEK = "seek"
        private const val STREAM_URL = "stream_url"
        private const val POSTER_URL = "poster_url"
    }

    /**
     * Returns the last playing song when the app was closed.
     * Fetches from SharedPreferences and if not found return null.
     */
    fun getLastPlayingSong(): SongObject? {
        val exist = lastPlayedPreferences.getBoolean(EXIST, false)

        if(!exist)
            return null

        val songObject = SongObject()
        songObject.songId = lastPlayedPreferences.getString(SONG_ID, "-1")!!
        songObject.songName = lastPlayedPreferences.getString(SONG_NAME, "Unknown Song")!!
        songObject.singer = lastPlayedPreferences.getString(SINGER_NAME, "Unknown Singer")!!
        songObject.songStreamUrl = lastPlayedPreferences.getString(STREAM_URL, "")!!
        songObject.songPosterUrl = lastPlayedPreferences.getString(POSTER_URL, "")!!
        songObject.storedPlaybackSeek = lastPlayedPreferences.getInt(SEEK, 0)
        return songObject
    }

    /**
     * Set The {@link com.k10.musicapp.datamodels.SongObject} to SharedPreference of "last_played"
     */
    suspend fun setLastPlaying(songObject: SongObject) {
        try {
            editPlayedPreferences.putString(SONG_ID, songObject.songId).apply()
            editPlayedPreferences.putString(SONG_NAME, songObject.songName).apply()
            editPlayedPreferences.putString(SINGER_NAME, songObject.singer).apply()
            editPlayedPreferences.putString(STREAM_URL, songObject.songStreamUrl).apply()
            editPlayedPreferences.putString(POSTER_URL, songObject.songPosterUrl).apply()
            editPlayedPreferences.putInt(SEEK, songObject.storedPlaybackSeek).apply()
            editPlayedPreferences.putBoolean(EXIST, true).apply()
        }catch (e: Exception){
            editPlayedPreferences.putBoolean(EXIST, false).apply()
        }

    }

    /**
     * Return if the last song was playing from a playlist
     * (Maybe) use Room for storing playlist.
     */
    fun getLastPlayingList(): List<SongObject>? {
        val songs: List<SongObject> = ArrayList<SongObject>()
        return songs
    }
}