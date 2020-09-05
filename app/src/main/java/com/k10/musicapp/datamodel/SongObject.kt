package com.k10.musicapp.datamodel

data class SongObject(
    var songId: String = "-1",
    var songName: String = "Unknown Song",
    var singer: String = "Unknown Singer",
    var songStreamUrl: String = "",
    var songPosterUrl: String = "",
    var storedPlaybackSeek: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if(other == null)
            return false
        if (other !is SongObject)
            return false
        if (songId != other.songId)
            return false
        if (songName != other.songName)
            return false
        if (songStreamUrl != other.songStreamUrl)
            return false
        if (songPosterUrl != other.songPosterUrl)
            return false
        return true
    }

    override fun hashCode(): Int {
        var result = songId.hashCode()
        result = 31 * result + songName.hashCode()
        result = 31 * result + singer.hashCode()
        result = 31 * result + songStreamUrl.hashCode()
        result = 31 * result + songPosterUrl.hashCode()
        result = 31 * result + storedPlaybackSeek
        return result
    }
}