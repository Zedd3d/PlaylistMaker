package com.zeddikus.playlistmaker.data.converters

import com.zeddikus.playlistmaker.data.db.TrackEntity
import com.zeddikus.playlistmaker.data.search.dto.TrackDto

object TrackDbConvertor {


    fun map(trackEntity: TrackEntity): TrackDto {
        return TrackDto(
            trackEntity.trackName,                                  // Название композиции
            trackEntity.artistName,                                 // Имя исполнителя
            trackEntity.trackTimeMillis,                            // Продолжительность трека
            trackEntity.artworkUrl100,                              // Ссылка на изображение обложки
            trackEntity.trackId,                                    // Идентификатор трека
            trackEntity.primaryGenreName,                           // Название жанра
            trackEntity.collectionName,                             // Название альбома
            trackEntity.releaseDate,                                // Дата выхода
            trackEntity.country,                                    // страна
            trackEntity.previewUrl                                  // URL предпросмотра
        )
    }

    fun map(trackDto: TrackDto): TrackEntity {
        return TrackEntity(
            trackId = trackDto.trackId
                ?: "",                                    // Идентификатор трека
            trackName = trackDto.trackName,                                  // Название композиции
            artistName = trackDto.artistName,                                 // Имя исполнителя
            trackTimeMillis = trackDto.trackTimeMillis,                            // Продолжительность трека
            artworkUrl100 = trackDto.artworkUrl100,                              // Ссылка на изображение обложки
            primaryGenreName = trackDto.primaryGenreName,                           // Название жанра
            collectionName = trackDto.collectionName,                             // Название альбома
            releaseDate = trackDto.releaseDate,                                // Дата выхода
            country = trackDto.country,                                    // страна
            previewUrl = trackDto.previewUrl                                  // URL предпросмотра
        )
    }
}