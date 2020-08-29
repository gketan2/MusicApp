package com.k10.musicapp.wrappers

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.k10.musicapp.services.PlayerState

class PlaybackWrapper<T> {

    @Nullable
    var status: Int

    @Nullable
    var data: T

    @Nullable
    var message: String

    constructor(@NonNull Status: Int, @Nullable data: T, @Nullable message: String) {
        this.status = Status
        this.data = data
        this.message = message
    }

    companion object {
        fun <T> info(data: T, state: Int): PlaybackWrapper<T> {
            return PlaybackWrapper(
                state,
                data,
                "success"
            )
        }

        fun <T> playing(): PlaybackWrapper<T> {
            return PlaybackWrapper(
                PlayerState.PLAYING,
                null as T,
                "success"
            )
        }

        fun <T> paused(): PlaybackWrapper<T> {
            return PlaybackWrapper(
                PlayerState.PAUSED,
                null as T,
                "success"
            )
        }

        fun <T> complete(): PlaybackWrapper<T> {
            return PlaybackWrapper(
                PlayerState.PLAYBACK_COMPLETE,
                null as T,
            "complete"
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

        fun <T> idle(): PlaybackWrapper<T> {
            return PlaybackWrapper(
                PlayerState.IDLE,
                null as T,
                "loading"
            )
        }

        fun <T> none(): PlaybackWrapper<T> {
            return PlaybackWrapper(
                PlayerState.NONE,
                null as T,
                "destroyed"
            )
        }
    }
}