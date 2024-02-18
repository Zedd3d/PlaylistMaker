package com.zeddikus.playlistmaker.data.converters

import com.zeddikus.playlistmaker.data.search.dto.TrackDto
import com.zeddikus.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

object TrackConvertor {
    fun map(searchResult: List<TrackDto>): List<Track> {
        return searchResult.map {
            map(it)
        }
    }

    fun map(trackDto: TrackDto): Track {
        return Track(
            trackDto.trackName ?: "",
            trackDto.artistName ?: "",
            trackDto.trackTimeMillis ?: 0L,
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackDto.trackTimeMillis)
                ?: "00:00",
            trackDto.artworkUrl100 ?: "",
            trackDto.trackId ?: "0",
            trackDto.primaryGenreName ?: "",
            trackDto.collectionName ?: "",
            trackDto.releaseDate ?: "0001-01-01T00:00:00Z",
            trackDto.country ?: "",
            trackDto.previewUrl ?: ""
        )
    }

    fun map(track: Track): TrackDto {
        return TrackDto(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.primaryGenreName,
            track.collectionName,
            track.releaseDate,
            track.country,
            track.previewUrl
        )
    }


}