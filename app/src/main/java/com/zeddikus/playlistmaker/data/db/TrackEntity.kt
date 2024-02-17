package com.zeddikus.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey(autoGenerate = false)
    val trackId: String,                                    // Идентификатор трека
    val trackName: String?,                                  // Название композиции
    val artistName: String?,                                 // Имя исполнителя
    val trackTimeMillis: Long?,                              // Продолжительность трека
    val artworkUrl100: String?,                              // Ссылка на изображение обложки
    val primaryGenreName: String?,                           // Название жанра
    val collectionName: String?,                             // Название альбома
    val releaseDate: String?,                                // Дата выхода
    val country: String?,                                    // страна
    val previewUrl: String?,                                 // URL предпросмотра
    var addTime: Long = 0                                    // Время добавления
)
