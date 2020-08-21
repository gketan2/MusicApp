package com.k10.musicapp.datamodel

data class PlaybackObject(
    var playedMilli: Int = 0,
    var durationMilli: Int = 0,
    var playedMinute: Int = 0,
    var playedSecond: Int = 0,
    var durationMinute:Int = 0,
    var durationSecond:Int = 0
)