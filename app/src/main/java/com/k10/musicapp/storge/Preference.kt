package com.k10.musicapp.storge

import com.k10.musicapp.datamodel.SongObject

class Preference {

    /**
     * Returns the last playing song when the app was closed.
     * Fetches from SharedPreferences and if not found return null.
     */
    fun getLastPlayingSong(): SongObject? {
//        if()
        return null
    }

    /**
     * Return if the last song was playing from a playlist
     * (Maybe) use Room for storing playlist.
     */
    fun getLastPlayingList(): List<SongObject>? {
        return null
    }
}