package com.k10.musicapp.wrappers

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.k10.musicapp.services.PlayerState

class PlaybackWrapper<T> {

    @Nullable
    var status: PlayerState

    @Nullable
    var data: T

    @Nullable
    var message: String

    constructor(@NonNull Status: PlayerState, @Nullable data: T, @Nullable message: String){
        this.status =Status
        this.data = data
        this.message = message
    }

    companion object {
        fun <T> info(data: T, state: PlayerState): PlaybackWrapper<T> {
            return PlaybackWrapper(
                state,
                data,
                "success"
            )
        }

        fun <T> error(msg: String): PlaybackWrapper<T> {
            return PlaybackWrapper(
                PlayerState.ERROR,
                null as T,
                msg
            )
        }

        fun <T> loading(): PlaybackWrapper<T> {
            return PlaybackWrapper(
                PlayerState.PREPARING,
                null as T,
                "loading"
            )
        }

        fun <T> empty(): PlaybackWrapper<T> {
            return PlaybackWrapper(
                PlayerState.IDLE,
                null as T,
                "loading"
            )
        }
    }
}