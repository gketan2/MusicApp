package com.k10.musicapp.datamodel

data class SongObject(
    var songId: String = "-1",
    var songName: String = "Unknown Song",
    var  singer: String = "Unknown Singer",
    var songStreamUrl: String = "",
    var songPosterUrl: String = "https://cdn.pixabay.com/photo/2018/05/21/12/49/clipart-3418189__340.png",
    var storedPlaybackSeek: Int = 0
//    var songIndexInList: Int = -1
//    var songLength: Int = 0,
//    var currentSeek: Int = 0
)