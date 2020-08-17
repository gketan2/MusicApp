package com.k10.musicapp.repository

import androidx.lifecycle.MediatorLiveData
import com.k10.musicapp.datamodel.SongObject

class SongStateRepository {

    companion object {
        //until hilt is not setup
        private var songStateRepository: SongStateRepository? = null
        fun getInstance(): SongStateRepository {
            if (songStateRepository == null) {
                songStateRepository = SongStateRepository()
            }
            return songStateRepository!!
        }
    }

    private val playbackState: MediatorLiveData<Int> = MediatorLiveData()
    fun getPlaybackState(): MediatorLiveData<Int> {
        return playbackState
    }

    fun setPlaybackState(state: Int) {
        playbackState.value = state
    }

    private val currentSong: MediatorLiveData<SongObject> = MediatorLiveData()
    fun getCurrentSong(): MediatorLiveData<SongObject> {
        return currentSong
    }

    fun setCurrentSong(song: SongObject) {
        currentSong.value = song
    }

    fun playSong(songObject: SongObject){

    }
}