package com.k10.musicapp.datamodel

data class SongObject(
    var songId: String = "-1",
    var songName: String = "Unknown Song",
    var singer: String = "Unknown Singer",
    var songStreamUrl: String = "",
    var songPosterUrl: String = "https://upload.wikimedia.org/wikipedia/commons/4/45/Cliparts_%28examples%29.png",
    var storedPlaybackSeek: Int = 0
//    var songIndexInList: Int = -1
//    var songLength: Int = 0,
//    var currentSeek: Int = 0
)