package com.k10.musicapp.datamodel

data class SongObject (
    var songId: String = "-1",
    var songName: String = "Unknown",
    var songStreamUrl: String = "",
    var songPosterUrl: String? = null,
    var songLength: Int = 0,
    var currentSeek: Int = 0
)