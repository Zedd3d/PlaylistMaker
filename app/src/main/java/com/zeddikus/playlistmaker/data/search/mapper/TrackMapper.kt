package com.zeddikus.playlistmaker.data.search.mapper

import com.zeddikus.playlistmaker.data.search.dto.TrackDto
import com.zeddikus.playlistmaker.domain.sharing.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

object TrackMapper {
    fun map(searchResult: List<TrackDto>): List<Track> {
        return searchResult.map {
            Track(
                it.trackName ?: "",
                it.artistName ?: "",
                it.trackTimeMillis ?: 0L,
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
                    ?: "00:00",
                it.artworkUrl100 ?: "",
                it.trackId ?: "0",
                it.primaryGenreName ?: "",
                it.collectionName ?: "",
                it.releaseDate ?: "0001-01-01T00:00:00Z",
                it.country ?: "",
                it.previewUrl ?: ""
            )
        }
    }
}