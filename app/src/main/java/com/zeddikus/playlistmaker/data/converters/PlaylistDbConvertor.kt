package com.zeddikus.playlistmaker.data.converters

import com.zeddikus.playlistmaker.data.db.PlaylistEntity
import com.zeddikus.playlistmaker.data.db.TrackInPlaylistEntity
import com.zeddikus.playlistmaker.data.mediatec.playlists.dto.PlaylistDto
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.domain.search.model.Track

object PlaylistDbConvertor {

    fun mapToPlayList(playlistDto: PlaylistDto): Playlist {
        return Playlist(
            playlistId = playlistDto.playlistId,
            playlistName = playlistDto.playlistName ?: "",
            playlistDescription = playlistDto.playlistDescription ?: "",
            playlistCover = playlistDto.playlistCover ?: "",
            tracksCount = playlistDto.tracksCount,
            playlistTimeMillis = playlistDto.playlistTimeMillis,
            System.currentTimeMillis()
        )
    }

    fun mapToPlayList(playlistEntity: PlaylistEntity): Playlist {
        return mapToPlayList(map(playlistEntity))
    }

    fun map(playlistEntity: PlaylistEntity): PlaylistDto {
        return PlaylistDto(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.playlistCover,
            playlistEntity.tracksCount,
            playlistEntity.playlistTimeMillis,
            playlistEntity.addTime

        )
    }

    fun map(playlistDto: PlaylistDto): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlistDto.playlistId,
            playlistName = playlistDto.playlistName,
            playlistDescription = playlistDto.playlistDescription,
            playlistCover = playlistDto.playlistCover,
            tracksCount = playlistDto.tracksCount,
            playlistTimeMillis = playlistDto.playlistTimeMillis,
            System.currentTimeMillis()
        )
    }

    fun map(playlist: Playlist): PlaylistDto {
        return PlaylistDto(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistCover = playlist.playlistCover,
            tracksCount = playlist.tracksCount,
            System.currentTimeMillis()
        )
    }

    fun mapToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return map(map(playlist))
    }

    fun mapToTrackInPlaylistEntity(playlist: Playlist, track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            tableStringId = 0,
            playlistID = playlist.playlistId,
            trackId = track.trackId,                                    // Идентификатор трека
            trackName = track.trackName,                                  // Название композиции
            artistName = track.artistName,                                 // Имя исполнителя
            trackTimeMillis = track.trackTimeMillis,                            // Продолжительность трека
            artworkUrl100 = track.artworkUrl100,                              // Ссылка на изображение обложки
            primaryGenreName = track.primaryGenreName,                           // Название жанра
            collectionName = track.collectionName,                             // Название альбома
            releaseDate = track.releaseDate,                                // Дата выхода
            country = track.country,                                    // страна
            previewUrl = track.previewUrl,
            addTime = System.currentTimeMillis()
        )
    }
}